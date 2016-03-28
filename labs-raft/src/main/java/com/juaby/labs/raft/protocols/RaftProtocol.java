package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.*;
import com.juaby.labs.rpc.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ObjIntConsumer;


/**
 * Implementation of the <a href="https://github.com/ongardie/dissertation">RAFT consensus protocol</a> in JGroups<p/>
 * [1] https://github.com/ongardie/dissertation
 *
 * @author Bela Ban
 * @since 0.1
 */

/** Implementation of the RAFT consensus protocol */
public class RaftProtocol implements Runnable, Settable, DynamicMembership {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    // When moving to JGroups -> add to jg-protocol-ids.xml
    protected static final byte[] raft_id_key = Util.raftIdKey("raft-id");
    protected static final short RAFT_ID = 521;

    // When moving to JGroups -> add to jg-magic-map.xml
    protected static final short APPEND_ENTRIES_REQ = 2000;
    protected static final short APPEND_ENTRIES_RSP = 2001;
    protected static final short APPEND_RESULT = 2002;
    protected static final short INSTALL_SNAPSHOT_REQ = 2003;

    /** The identifier of this node. Needs to be unique and an element of members. Must not be null, writable=false
     */
    protected String raft_id;

    /** The set of members defining the Raft cluster */
    protected final List<String> members = new ArrayList<>();

    /** Majority needed to achieve consensus; computed from members, writable=false */
    protected int majority = -1;

    /** If true, we can change 'members' at runtime */
    protected boolean dynamic_view_changes = true;

    /** The fully qualified name of the class implementing Log */
    protected String log_class = "org.jgroups.protocols.raft.LevelDBLog";

    /** Arguments to the log impl, e.g. k1=v1,k2=v2. These will be passed to init() */
    protected String log_args;

    /** The name of the log. The logical name of the channel (if defined) is used by default.
     Note that logs for different processes on the same host need to be different
     */
    protected String log_name;

    /** The name of the snapshot. By default, <log_name>.snapshot will be used */
    protected String snapshot_name;

    /** Interval (ms) at which AppendEntries messages are resent to members which haven't received them yet */
    protected long resend_interval = 1000;

    /** Max number of bytes a log can have until a snapshot is created */
    protected int max_log_size = 1_000_000;

    /** task firing every resend_interval ms to send AppendEntries msgs to mbrs which are missing them */
    protected Future<?> resend_task;

    protected StateMachine state_machine;

    protected boolean state_machine_loaded;

    protected Log log_impl;

    protected RequestTable<String> request_table;
    protected CommitTable commit_table;

    protected final List<RoleChange> role_change_listeners = new ArrayList<>();

    // Set to true during an addServer()/removeServer() op until the change has been committed
    protected final AtomicBoolean members_being_changed = new AtomicBoolean(false);

    /** The current role (follower, candidate or leader). Every node starts out as a follower */
    /** impl_lock */
    protected volatile RaftImpl impl = new Follower(this);

    protected Endpoint local_addr;
    protected TimeScheduler timer;

    /** The current leader (can be null if there is currently no leader) */
    protected volatile Endpoint leader;

    /** The current term. Incremented when this node becomes a candidate, or set when a higher term is seen */
    /** The current term */
    protected int current_term;

    /** Index of the highest log entry appended to the log */
    protected int last_appended;

    /** Index of the highest committed log entry */
    protected int commit_index;

    /** Is a snapshot in progress */
    protected boolean snapshotting;

    protected int log_size_bytes; // keeps counts of the bytes added to the log

    private int MAX_APPEND = Runtime.getRuntime().availableProcessors();
    private ExecutorService appendThreadPool = Executors.newFixedThreadPool(MAX_APPEND, new RpcThreadFactory("APPEND"));

    public Endpoint address() {
        return local_addr;
    }

    public String raftId() {
        return raft_id;
    }

    public RaftProtocol raftId(String id) {
        this.raft_id = id;
        return this;
    }

    public int majority() {
        synchronized (members) {
            return majority;
        }
    }

    public String logClass() {
        return log_class;
    }

    public RaftProtocol logClass(String clazz) {
        log_class = clazz;
        return this;
    }

    public String logArgs() {
        return log_args;
    }

    public RaftProtocol logArgs(String args) {
        log_args = args;
        return this;
    }

    public String logName() {
        return log_name;
    }

    public RaftProtocol logName(String name) {
        log_name = name;
        return this;
    }

    public String snapshotName() {
        return snapshot_name;
    }

    public RaftProtocol snapshotName(String name) {
        snapshot_name = name;
        return this;
    }

    public long resendInterval() {
        return resend_interval;
    }

    public RaftProtocol resendInterval(long val) {
        resend_interval = val;
        return this;
    }

    public int maxLogSize() {
        return max_log_size;
    }

    public RaftProtocol maxLogSize(int val) {
        max_log_size = val;
        return this;
    }

    /** Current leader */
    public String getLeader() {
        return leader != null ? leader.toString() : "none";
    }

    public Endpoint leader() {
        return leader;
    }

    public RaftProtocol leader(Endpoint new_leader) {
        this.leader = new_leader;
        return this;
    }

    public boolean isLeader() {
        return Objects.equals(leader, local_addr);
    }

    public Logger getLog() {
        return this.log;
    }

    public RaftProtocol stateMachine(StateMachine sm) {
        this.state_machine = sm;
        return this;
    }

    public StateMachine stateMachine() {
        return state_machine;
    }

    public int currentTerm() {
        return current_term;
    }

    public int lastAppended() {
        return last_appended;
    }

    public int commitIndex() {
        return commit_index;
    }

    public Log log() {
        return log_impl;
    }

    public RaftProtocol log(Log new_log) {
        this.log_impl = new_log;
        return this;
    }

    public RaftProtocol addRoleListener(RoleChange c) {
        this.role_change_listeners.add(c);
        return this;
    }

    public RaftProtocol remRoleListener(RoleChange c) {
        this.role_change_listeners.remove(c);
        return this;
    }

    /** Is the resend task running */
    public boolean resendTaskRunning() {
        return resend_task != null && !resend_task.isDone();
    }

    /** List of members (logical names); majority is computed from it */
    public void setMembers(String list) {
        synchronized (members) {
            this.members.clear();
            this.members.addAll(new HashSet<>(Util.parseStringList(list, ",")));
            majority = members.size() / 2 + 1;
        }
    }

    public RaftProtocol members(List<String> list) {
        synchronized (members) {
            this.members.clear();
            this.members.addAll(new HashSet<>(list));
            majority = members.size() / 2 + 1;
        }
        return this;
    }

    public List<String> members() {
        synchronized (members) {
            return new ArrayList<>(members);
        }
    }

    /**
     * Sets current_term if new_term is bigger
     * @param new_term The new term
     * @return -1 if new_term is smaller, 0 if equal and 1 if new_term is bigger
     */
    public synchronized int currentTerm(final int new_term) {
        if (new_term < current_term) {
            return -1;
        }
        if (new_term > current_term) {
            log.trace("%s: changed term from %d -> %d", local_addr, current_term, new_term);
            current_term = new_term;
            log_impl.currentTerm(new_term);
            changeRole(Role.Follower);
            return 1;
        }
        return 0;
    }

    /** The current role */
    public String role() {
        RaftImpl tmp = impl;
        return tmp.getClass().getSimpleName();
    }

    /** Dumps the commit table */
    public String dumpCommitTable() {
        return commit_table != null ? commit_table.toString() : "n/a";
    }

    /** Number of log entries in the log */
    public int logSize() {
        final AtomicInteger count = new AtomicInteger(0);
        log_impl.forEach((entry, index) -> count.incrementAndGet());
        return count.intValue();
    }

    /** Number of bytes in the log */
    public int logSizeInBytes() {
        final AtomicInteger count = new AtomicInteger(0);
        log_impl.forEach((entry, index) -> count.addAndGet(entry.length()));
        return count.intValue();
    }

    /** Dumps the last N log entries */
    public String dumpLog(int last_n) {
        final StringBuilder sb = new StringBuilder();
        int to = last_appended, from = Math.max(1, to - last_n);
        log_impl.forEach((entry, index) -> sb.append("index=").append(index)
                .append(", term=").append(entry.term())
                .append(" (").append(entry.command().length).append(" bytes)\n"), from, to);
        return sb.toString();
    }

    /** Dumps all log entries */
    public String dumpLog() {
        return dumpLog(last_appended - 1);
    }

    public void logEntries(ObjIntConsumer<LogEntry> func) {
        log_impl.forEach(func);
    }

    public synchronized int createNewTerm() {
        return ++current_term;
    }

    protected synchronized void createRequestTable() {
        request_table = new RequestTable<>();
        // Populate with non-committed entries (from log) (https://github.com/belaban/jgroups-raft/issues/31)
        for (int i = this.commit_index + 1; i <= this.last_appended; i++) {
            request_table.create(i, raft_id, null);
        }
    }

    protected void createCommitTable() {
        List<Endpoint> mbrs = new ArrayList<Endpoint>();
        //TODO
        for (String mbr : members()) {
            String[] mbrArr = mbr.split(":");
            mbrs.add(new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1])));
        }
        mbrs.remove(local_addr);
        commit_table = new CommitTable(mbrs, last_appended + 1);
    }

    public synchronized boolean updateTermAndLeader(int term, Endpoint new_leader) {
        if (leader == null || (new_leader != null && !leader.equals(new_leader))) {
            leader = new_leader;
        }
        if (term > current_term) {
            current_term = term;
            return true;
        }
        return false;
    }

    /** Adds a new server to members. Prevents duplicates */
    public CompletableFuture<byte[]> addServer(String name) throws Exception {
        return changeMembers(name, InternalCommand.Type.addServer);
    }

    /** Removes a new server from members */
    public CompletableFuture<byte[]> removeServer(String name) throws Exception {
        return changeMembers(name, InternalCommand.Type.removeServer);
    }

    protected CompletableFuture<byte[]> changeMembers(String name, InternalCommand.Type type) throws Exception {
        if (!dynamic_view_changes) {
            throw new Exception("dynamic view changes are not allowed; set dynamic_view_changes to true to enable it");
        }
        if (leader == null || (local_addr != null && !leader.equals(local_addr))) {
            throw new IllegalStateException("I'm not the leader (local_addr=" + local_addr + ", leader=" + leader + ")");
        }

        if (members_being_changed.compareAndSet(false, true)) {
            InternalCommand cmd = new InternalCommand(type, name);
            byte[] buf = SerializeTool.serialize(cmd);
            return setAsync(buf, 0, buf.length, cmd);
        } else {
            throw new IllegalStateException(type + "(" + name + ") cannot be invoked as previous operation has not yet been committed");
        }
    }

    void _addServer(String name) {
        if (name == null) {
            return;
        }
        synchronized (members) {
            if (!members.contains(name)) {
                members.add(name);
                majority = members.size() / 2 + 1;

                handleChangeMember(members);
            }
        }
    }

    void _removeServer(String name) {
        if (name == null) {
            return;
        }
        synchronized (members) {
            if (members.remove(name)) {
                majority = members.size() / 2 + 1;

                handleChangeMember(members);
            }
        }
    }

    /**
     * Creates a new snapshot and truncates the log. See https://github.com/belaban/jgroups-raft/issues/7 for details
     */
    /** Creates a new snapshot and truncates the log */
    public synchronized void snapshot() throws Exception {
        // todo: make sure all requests are blocked while dumping the snapshot
        if (snapshotting) {
            log.error("%s: cannot create snapshot; snapshot is being created by another thread");
            return;
        }
        try {
            snapshotting = true;
            doSnapshot();
        } finally {
            snapshotting = false;
        }
    }

    /**
     * Loads the log entries from [first .. commit_index] into the state machine
     */
    /** Reads snapshot (if present) and log entries up to
     commit_index and applies them to the state machine
     */
    public synchronized void initStateMachineFromLog(boolean force) throws Exception {
        if (state_machine != null) {
            if (!state_machine_loaded || force) {
                int snapshot_offset = 0;
                try (InputStream input = new FileInputStream(snapshot_name)) {
                    state_machine.readContentFrom(new DataInputStream(input));
                    snapshot_offset = 1;
                    log.debug("%s: initialized state machine from snapshot %s", local_addr, snapshot_name);
                } catch (FileNotFoundException fne) {
                    // log.debug("snapshot %s not found, initializing state machine from persistent log", snapshot_name);
                }

                int from = Math.max(1, log_impl.firstAppended() + snapshot_offset), to = commit_index, count = 0;
                for (int i = from; i <= to; i++) {
                    LogEntry log_entry = log_impl.get(i);
                    if (log_entry == null) {
                        log.error("%s: log entry for index %d not found in log", local_addr, i);
                        break;
                    }
                    if (log_entry.command != null) {
                        if (log_entry.internal) {
                            executeInternalCommand(null, log_entry.command, log_entry.offset, log_entry.length);
                        } else {
                            state_machine.apply(log_entry.command, log_entry.offset, log_entry.length);
                            count++;
                        }
                    }
                }
                state_machine_loaded = true;
                if (count > 0) {
                    log.debug("%s: applied %d entries from the log (%d - %d) to the state machine", local_addr, count, from, to);
                }
            }
        }
    }

    public void init() throws Exception {
        //super.init(); //TODO
        timer = new DefaultTimeScheduler(); //TODO
        if (raft_id == null) {
            throw new IllegalStateException("raft_id must not be null");
        }

        // we can only add/remove 1 member at a time (section 4.1 of [1])
        if (dynamic_view_changes) {
            //TODO
        }
        synchronized (members) {
            Set<String> tmp = new HashSet<>(members);
            if (tmp.size() != members.size()) {
                log.error("members (%s) contains duplicates; removing them and setting members to %s", members, tmp);
                members.clear();
                members.addAll(tmp);
            }

            majority = members.size() / 2 + 1;
        }

        String ip = NetworkUtils.getIp();
        for (String mbr : members) {
            String[] mbrArr = mbr.split(":");
            if (mbrArr[0].equals(ip)) {
                local_addr = new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1]));
                break;
            }
        }

        // Set an AddressGenerator in channel which generates ExtendedUUIDs and adds the raft_id to the hashmap
        // put(raft_id_key, Util.stringToBytes(raft_id))); TODO

        // if we're the leader, check if the view contains no duplicate raft-ids
        /**
        if (duplicatesInMember()) {
            log.error("view contains duplicate raft-ids: %s", view);
        }
         */
    }

    public void start() throws Exception {
        //super.start();    //TODO
        if (log_class == null) {
            throw new IllegalStateException("log_class has to be defined");
        }

        if (!(local_addr instanceof Endpoint)) {
            throw new IllegalStateException("local address must be an ExtendedUUID but is a " + local_addr.getClass().getSimpleName());
        }

        Class<? extends Log> clazz = Util.loadClass(log_class, getClass());
        log_impl = clazz.newInstance();
        Map<String, String> args = null;
        if (log_args != null && !log_args.isEmpty()) {
            args=Util.parseCommaDelimitedProps(log_args);
        } else {
            args = new HashMap<>();
        }
        if (log_name == null) {
            log_name = raft_id;
        }
        snapshot_name = log_name;
        log_name = createLogName(log_name, "log");
        snapshot_name = createLogName(snapshot_name, "snapshot");

        log_impl.init(log_name, args);
        last_appended = log_impl.lastAppended();
        commit_index = log_impl.commitIndex();
        current_term = log_impl.currentTerm();
        log.trace("set last_appended=%d, commit_index=%d, current_term=%d", last_appended, commit_index, current_term);
        initStateMachineFromLog(false);
        log_size_bytes = logSizeInBytes();

        if (!members.contains(raft_id)) {
            throw new IllegalStateException(String.format("raft-id %s is not listed in members %s", raft_id, this.members));
        }
    }

    public void stop() {
        //super.stop(); TODO
        impl.destroy();
    }

    /**
     * The blocking equivalent of {@link #setAsync(byte[], int, int)}. Used to apply a change
     * across all cluster nodes via consensus.
     * @param buf The serialized command to be applied (interpreted by the caller)
     * @param offset The offset into the buffer
     * @param length The length of the buffer
     * @return The serialized result (to be interpreted by the caller)
     * @throws Exception ExecutionException or InterruptedException
     */
    public byte[] set(byte[] buf, int offset, int length) throws Exception {
        CompletableFuture<byte[]> future = setAsync(buf, offset, length, null);
        return future.get();
    }

    /**
     * Time bounded blocking get(). Returns when the result is available, or a timeout (or exception) has occurred
     * @param buf The buffer
     * @param offset The offset into the buffer
     * @param length The length of the buffer
     * @param timeout The timeout
     * @param unit The unit of the timeout
     * @return The serialized result
     * @throws Exception ExecutionException when the execution failed, InterruptedException, or TimeoutException when
     * the timeout elapsed without getting an exception.
     */
    public byte[] set(byte[] buf, int offset, int length, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<byte[]> future = setAsync(buf, offset, length, null);
        return future.get(timeout, unit);
    }

    /**
     * Called by a building block to apply a change to all state machines in a cluster. This starts the consensus
     * protocol to get a majority to commit this change.<p/>
     * This call is non-blocking and returns a future as soon as the AppendEntries message has been sent.<p/>
     * Only applicable on the leader
     * @param buf The command
     * @param offset The offset into the buffer
     * @param length The length of the buffer
     * @return A CompletableFuture. Can be used to wait for the result (sync). A blocking caller could call
     *         set(), then call future.get() to block for the result.
     */
    public CompletableFuture<byte[]> setAsync(byte[] buf, int offset, int length) {
        return setAsync(buf, offset, length, null);
    }

    protected CompletableFuture<byte[]> setAsync(byte[] buf, int offset, int length, InternalCommand cmd) {
        if (leader == null || (local_addr != null && !leader.equals(local_addr))) {
            throw new IllegalStateException("I'm not the leader (local_addr=" + local_addr + ", leader=" + leader + ")");
        }
        if (buf == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }

        CompletableFuture<byte[]> retval = new CompletableFuture<>();
        int prev_index = 0, curr_index = 0, prev_term = 0, curr_term = 0, commit_idx = 0;

        RequestTable<String> reqtab = request_table;
        if (reqtab == null) {
            retval.completeExceptionally(new IllegalStateException("request table was null on " + impl.getClass().getSimpleName()));
            return retval;
        }

        // 1. Append to the log
        synchronized (this) {
            prev_index = last_appended;
            curr_index = ++last_appended;
            curr_term = current_term;
            commit_idx = commit_index;
            LogEntry entry = log_impl.get(prev_index);
            prev_term = entry != null ? entry.term : 0;

            LogEntry appendEntry = new LogEntry(curr_term, buf, offset, length, cmd != null);
            log_impl.append(curr_index, true, appendEntry);

            if (cmd != null) {
                executeInternalCommand(cmd, null, 0, 0);
            }

            // 2. Add the request to the client table, so we can return results to clients when done
            reqtab.create(curr_index, raft_id, retval);

            // 3. Multicast an AppendEntries message (exclude self)
            List<Endpoint> mbrs = new ArrayList<Endpoint>();
            for (String mbr : members()) {
                String[] mbrArr = mbr.split(":");
                mbrs.add(new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1])));
            }
            mbrs.remove(local_addr);
            AppendEntriesRequest request = new AppendEntriesRequest(curr_term, this.local_addr, prev_index, prev_term, curr_term, commit_idx, cmd != null);
            request.setEntries(new LogEntry[] {appendEntry});
            for (Endpoint endpoint : mbrs) {
                appendThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Cache.getRaftService(endpoint).appendEntries(request, new RpcCallback<AppendEntriesResponse, Boolean>() {
                            @Override
                            public Boolean callback(AppendEntriesResponse response) {
                                impl.handleAppendEntriesResponse(response.getSrc(), response.term(), response.getResult());
                                return true;
                            }
                        });
                    }
                });
            }
        }

        snapshotIfNeeded(length);

        // 4. Return CompletableFuture
        return retval;
    }

    /**
     * Runs (on the leader) every resend_interval ms: checks if all members in the commit table have received
     * all messages and resends AppendEntries messages to members who haven't. <p/>
     * For each member a next-index and match-index is maintained: next-index is the index of the next message to send to
     * that member (initialized to last-applied) and match-index is the index of the highest message known to have
     * been received by the member.<p/>
     * Messages are resent to a given member long as that member's match-index is smaller than its next-index. When
     * match_index == next_index, message resending for that member is stopped. When a new message is appended to the
     * leader's log, next-index for all members is incremented and resending starts again.
     */
    @Override
    public void run() {
        commit_table.forEach(this::sendAppendEntriesMessage);
    }

    protected void sendAppendEntriesMessage(Endpoint member, CommitTable.Entry entry) {
        int next_index = entry.nextIndex(), commit_idx = entry.commitIndex(), match_index = entry.matchIndex();
        if (next_index < log().firstAppended()) {
            if (entry.snapshotInProgress(true)) {
                try {
                    sendSnapshotTo(member); // will reset snapshot_in_progress // todo: run in separate thread
                } catch (Exception e) {
                    log.error("%s: failed sending snapshot to %s: next_index=%d, first_applied=%d",
                            local_addr, member, next_index, log().firstAppended());
                }
            }
            return;
        }

        if (this.last_appended >= next_index) {
            int to = entry.sendSingleMessage() ? next_index : last_appended;
            for (int i = Math.max(next_index, 1); i <= to; i++) {  // i=match_index+1 ?
                if (log.isTraceEnabled()) {
                    log.trace("%s: resending %d to %s\n", local_addr, i, member);
                }
                resend(member, i);
            }
            return;
        }

        if (this.last_appended > match_index) {
            int index = this.last_appended;
            if (index > 0) {
                log.trace("%s: resending %d to %s\n", local_addr, index, member);
                resend(member, index);
            }
            return;
        }

        if (this.commit_index > commit_idx) { // send an empty AppendEntries message as commit message
            AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(current_term, this.local_addr, 0, 0, 0, this.commit_index, false);
            appendEntriesRequest.setEntries(new LogEntry[] {});
            AppendEntriesResponse response = Cache.getRaftService(member).appendEntries(appendEntriesRequest);
            AppendResult result = response.getResult();
            impl.handleAppendEntriesResponse(response.getSrc(), response.term(), result);
            // TODO
            return;
        }

        if (this.commit_index < this.last_appended) { // fixes https://github.com/belaban/jgroups-raft/issues/30
            for (int i = this.commit_index + 1; i <= this.last_appended; i++) {
                resend(member, i);
            }
        }
    }

    protected void resend(Endpoint target, int index) {
        LogEntry entry = log_impl.get(index);
        if (entry == null) {
            log.error("%s: resending of %d failed; entry not found", local_addr, index);
            return;
        }
        LogEntry prev = log_impl.get(index - 1);
        int prev_term = prev != null ? prev.term : 0;

        AppendEntriesRequest appendEntriesRequest = new AppendEntriesRequest(current_term, this.local_addr, index - 1, prev_term, entry.term, commit_index, entry.internal);
        appendEntriesRequest.setEntries(new LogEntry[] {entry});
        AppendEntriesResponse response = Cache.getRaftService(target).appendEntries(appendEntriesRequest);
        AppendResult result = response.getResult();
        impl.handleAppendEntriesResponse(response.getSrc(), response.term(), result);
        //TODO
    }

    protected void doSnapshot() throws Exception {
        if (state_machine == null) {
            throw new IllegalStateException("state machine is null");
        }
        try (OutputStream output = new FileOutputStream(snapshot_name)) {
            state_machine.writeContentTo(new DataOutputStream(output));
        }
        log_impl.truncate(commitIndex());
    }

    protected boolean snapshotExists() {
        File file = new File(snapshot_name);
        return file.exists();
    }

    public boolean deleteSnapshot() {
        File file = new File(snapshot_name);
        return file.delete();
    }

    protected synchronized void sendSnapshotTo(Endpoint dest) throws Exception {
        try {
            if (snapshotting) {
                return;
            }

            snapshotting = true;

            LogEntry last_committed_entry = log_impl.get(commitIndex());
            int last_index = commit_index, last_term = last_committed_entry.term;
            doSnapshot();

            // todo: use streaming approach (STATE$StateOutputStream, BlockingInputStream from JGroups)
            byte[] data = Files.readAllBytes(Paths.get(snapshot_name));
            log.debug("%s: sending snapshot (%s) to %s", local_addr, Util.printBytes(data.length), dest);

            InstallSnapshotRequest request = new InstallSnapshotRequest(currentTerm(), leader(), last_index, last_term);
            request.setData(data);
            AppendEntriesResponse response = Cache.getRaftService(dest).installSnapshot(request);
            AppendResult result = response.getResult();
            impl.handleAppendEntriesResponse(response.getSrc(), response.term(), result);
            // TODO
        } finally {
            snapshotting = false;
            if (commit_table != null) {
                commit_table.snapshotInProgress(dest, false);
            }
        }
    }

    /**
     * Received a majority of votes for the entry at index. Note that indices may be received out of order, e.g. if
     * we have modifications at indixes 4, 5 and 6, entry[5] might get a majority of votes (=committed)
     * before entry[3] and entry[6].<p/>
     * The following things are done:
     * <ul>
     *     <li>See if commit_index can be moved to index (incr commit_index until a non-committed entry is encountered)</li>
     *     <li>For each committed entry, apply the modification in entry[index] to the state machine</li>
     *     <li>For each committed entry, notify the client and set the result (CompletableFuture)</li>
     * </ul>
     *
     * @param index The index of the committed entry.
     */
    protected synchronized void handleCommit(int index) {
        try {
            for (int i = commit_index + 1; i <= index; i++) {
                if (request_table.isCommitted(i)) {
                    applyCommit(i);
                    commit_index = Math.max(commit_index, i);
                }
            }
        } catch (Throwable t) {
            log.error("failed applying commit %d: %s", index, t);
        }
    }

    /**
     * Tries to advance commit_index up to leader_commit, applying all uncommitted log entries to the state machine
     * @param leader_commit The commit index of the leader
     */
    protected synchronized boolean commitLogTo(int leader_commit) {
        try {
            for (int i = commit_index + 1; i <= Math.min(last_appended, leader_commit); i++) {
                applyCommit(i);
                commit_index = Math.max(commit_index, i);
            }
            return true;
        } catch (Throwable t) {
            log.error(local_addr + ": failed advancing commit_index (%d) to %d: %s", commit_index, leader_commit, t);
            return false;
        }
    }

    protected RaftProtocol append(int term, int index, byte[] data, int offset, int length, boolean internal) {
        LogEntry entry = new LogEntry(term, data, offset, length, internal);
        log_impl.append(index, true, entry);
        last_appended = log_impl.lastAppended();
        snapshotIfNeeded(length);
        return this;
    }

    protected void deleteAllLogEntriesStartingFrom(int index) {
        log_impl.deleteAllEntriesStartingFrom(index);
        last_appended = log_impl.lastAppended();
        commit_index = log_impl.commitIndex();
    }

    protected void snapshotIfNeeded(int bytes_added) {
        log_size_bytes += bytes_added;
        if (log_size_bytes >= max_log_size) {
            try {
                this.log.debug("%s: current log size is %d, exceeding max_log_size of %d: creating snapshot",
                        local_addr, log_size_bytes, max_log_size);
                snapshot();
                log_size_bytes = logSizeInBytes();
            } catch (Exception ex) {
                log.error("%s: failed snapshotting log: %s", local_addr, ex);
            }
        }
    }

    /** Applies the commit at index */
    protected void applyCommit(int index) throws Exception {
        // Apply the modifications to the state machine
        LogEntry log_entry = log_impl.get(index);
        if (log_entry == null) {
            throw new IllegalStateException(local_addr + ": log entry for index " + index + " not found in log");
        }
        if (state_machine == null) {
            throw new IllegalStateException(local_addr + ": state machine is null");
        }
        byte[] rsp = null;
        if (log_entry.internal) {
            InternalCommand cmd;
            try {
                cmd = new InternalCommand();
                SerializeTool.deserialize(log_entry.command, cmd);
                if (cmd.type() == InternalCommand.Type.addServer || cmd.type() == InternalCommand.Type.removeServer) {
                    members_being_changed.set(false); // new addServer()/removeServer() operations can now be started
                }
            } catch (Throwable t) {
                log.error("%s: failed unmarshalling internal command: %s", local_addr, t);
            }
        } else {
            rsp = state_machine.apply(log_entry.command, log_entry.offset, log_entry.length);
        }
        // Notify the client's CompletableFuture and then remove the entry in the client request table
        if (request_table != null) {
            if (rsp == null) {
                request_table.notifyAndRemove(index, null, 0, 0);
            } else {
                request_table.notifyAndRemove(index, rsp, 0, rsp.length);
            }
        }

        log_impl.commitIndex(index);
    }

    protected void handleChangeMember(List<String> members) {
        if (commit_table != null) {
            List<Endpoint> mbrs = new ArrayList<>();
            for (String mbr : members) {
                String[] mbrArr = mbr.split(":");
                mbrs.add(new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1])));
            }
            mbrs.remove(local_addr);
            commit_table.adjust(mbrs, last_appended + 1);
        }
    }

    protected void changeRole(Role new_role) {
        RaftImpl new_impl = new_role == Role.Follower ? new Follower(this) : new_role == Role.Candidate ? new Candidate(this) : new Leader(this);
        RaftImpl old_impl = impl;
        if (old_impl == null || !old_impl.getClass().equals(new_impl.getClass())) {
            if (old_impl != null) {
                old_impl.destroy();
            }
            new_impl.init();
            synchronized (this) {
                impl = new_impl;
            }
            log.trace("%s: changed role from %s -> %s", local_addr, old_impl == null ? "null" :
                    old_impl.getClass().getSimpleName(), new_impl.getClass().getSimpleName());
            notifyRoleChangeListeners(new_role);
        }
    }

    /** If cmd is not null, execute it. Else parse buf into InternalCommand then call cmd.execute() */
    protected void executeInternalCommand(InternalCommand cmd, byte[] buf, int offset, int length) {
        if (cmd == null) {
            try {
                cmd = new InternalCommand();
                SerializeTool.deserialize(buf, cmd);
            } catch (Exception ex) {
                log.error("%s: failed unmarshalling internal command: %s", local_addr, ex);
                return;
            }
        }

        try {
            cmd.execute(this);
        } catch (Exception ex) {
            log.error("%s: failed executing internal command %s: %s", local_addr, cmd, ex);
        }
    }

    protected synchronized void startResendTask() {
        if (resend_task == null || resend_task.isDone()) {
            resend_task = timer.scheduleWithFixedDelay(this, resend_interval, resend_interval, TimeUnit.MILLISECONDS);
        }
    }

    protected synchronized void stopResendTask() {
        if (resend_task != null) {
            resend_task.cancel(false);
            resend_task = null;
        }
    }

    protected static String createLogName(String name, String suffix) {
        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }
        boolean needs_suffix = !name.endsWith(suffix);
        String retval = name;
        if (!new File(name).isAbsolute()) {
            //TODO CHECK OS TYPE
            String dir = false ? File.separator + "tmp" : System.getProperty("java.io.tmpdir", File.separator + "tmp");
            retval = dir + File.separator + name;
        }
        return needs_suffix ? retval + suffix : retval;
    }

    protected void notifyRoleChangeListeners(Role role) {
        for (RoleChange ch : role_change_listeners) {
            try {
                ch.roleChanged(role);
            } catch (Throwable t) {
            }
        }
    }

    /**
     * Checks if a given view contains duplicate raft-ids. Uses key raft-id in ExtendedUUID to compare
     * @param
     * @return
     */
    protected boolean duplicatesInMember() {
        Set<String> mbrs = new HashSet<>();
        for (String addr : members()) {
            if (!mbrs.add(addr)) {
                return true;
            }
        }
        return false;
    }

    /** number of requests being processed */
    public int requestTableSize() {
        return request_table.size();
    }

    public interface RoleChange {
        void roleChanged(Role role);
    }

    public RaftImpl getRaftImpl() {
        return impl;
    }

}