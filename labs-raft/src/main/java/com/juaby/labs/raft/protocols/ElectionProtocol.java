package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.raft.util.DefaultTimeScheduler;
import com.juaby.labs.raft.util.TimeScheduler;
import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.NetworkUtils;
import com.juaby.labs.rpc.util.RpcCallback;
import com.juaby.labs.rpc.util.RpcThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Performs leader election. Starts an election timer on connect and starts an election when the timer goes off and
 * no heartbeats have been received. Runs a heartbeat task when leader.
 *
 * @author Bela Ban
 * @since 0.1
 */

/**
 * Protocol performing leader election according to the RAFT paper
 */
public class ElectionProtocol {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    // when moving to JGroups -> add to jg-protocol-ids.xml
    protected static final short ELECTION_ID = 520;

    // When moving to JGroups -> add to jg-magic-map.xml
    protected static final short VOTE_REQ = 3000;
    protected static final short VOTE_RSP = 3001;
    protected static final short HEARTBEAT_REQ = 3002;

    /**
     * Interval (in ms) at which a leader sends out heartbeats
     */
    protected long heartbeat_interval = 30;

    /**
     * Min election interval (ms)
     */
    protected long election_min_interval = 150;

    /**
     * Max election interval (ms). The actual election interval is computed as a random value in
     * range [election_min_interval..election_max_interval]
     */
    protected long election_max_interval = 300;

    /**
     * The address of the candidate this node voted for in the current term
     */
    protected Endpoint voted_for;

    /**
     * Votes collected for me in the current term (if candidate)
     * Number of votes this candidate received in the current term
     */
    protected int current_votes;

    /**
     * No election will ever be started if true; this node will always be a follower.
     * Used only for testing and may get removed. Don't use !
     */
    protected boolean no_elections;

    /**
     * Whether a heartbeat has been received before this election timeout kicked in. If false, the follower becomes
     * candidate and starts a new election
     */
    protected volatile boolean heartbeat_received = true;

    protected RaftProtocol raft; // direct ref instead of events
    protected Endpoint local_addr;
    protected TimeScheduler timer = new DefaultTimeScheduler();
    protected Future<?> election_task;
    protected Future<?> heartbeat_task;
    protected Role role = Role.Follower;

    private int MAX_HEART_BEAT = Runtime.getRuntime().availableProcessors();
    private int MAX_VOTE = Runtime.getRuntime().availableProcessors();
    private ExecutorService heartbeatThreadPool = Executors.newFixedThreadPool(MAX_HEART_BEAT, new RpcThreadFactory("HEART_BEAT"));
    private ExecutorService voteThreadPool = Executors.newFixedThreadPool(MAX_VOTE, new RpcThreadFactory("VOTE"));

    public long heartbeatInterval() {
        return heartbeat_interval;
    }

    public ElectionProtocol heartbeatInterval(long val) {
        heartbeat_interval = val;
        return this;
    }

    public long electionMinInterval() {
        return election_min_interval;
    }

    public ElectionProtocol electionMinInterval(long val) {
        election_min_interval = val;
        return this;
    }

    public long electionMaxInterval() {
        return election_max_interval;
    }

    public ElectionProtocol electionMaxInterval(long val) {
        election_max_interval = val;
        return this;
    }

    public boolean noElections() {
        return no_elections;
    }

    public ElectionProtocol noElections(boolean flag) {
        no_elections = flag;
        return this;
    }

    /**
     * The current role
     */
    public String role() {
        return role.toString();
    }

    /**
     * Is the heartbeat task running
     */
    public synchronized boolean isHeartbeatTaskRunning() {
        return heartbeat_task != null && !heartbeat_task.isDone();
    }

    /**
     * Is the election ttimer running
     */
    public synchronized boolean isElectionTimerRunning() {
        return election_task != null && !election_task.isDone();
    }

    public void init() throws Exception {
        //super.init();
        if (election_min_interval >= election_max_interval) {
            throw new Exception("election_min_interval (" + election_min_interval + ") needs to be smaller than " +
                    "election_max_interval (" + election_max_interval + ")");
        }
        timer = new DefaultTimeScheduler();
        raft = Cache.getRaftProtocol();
        startElectionTimer();
    }

    public void setLocalAddr(Endpoint local_addr) {
        this.local_addr = local_addr;
    }

    protected synchronized void handleHeartbeat(int term, Endpoint leader) {
        if (Objects.equals(local_addr, leader)) {
            return;
        }
        heartbeatReceived(true);
        if (role != Role.Follower || raft.updateTermAndLeader(term, leader)) {
            changeRole(Role.Follower);
            voteFor(null);
        }
    }

    protected boolean handleVoteRequest(Endpoint sender, int term, int last_log_term, int last_log_index) {
        if (Objects.equals(local_addr, sender)) {
            return false;
        }
        if (log.isTraceEnabled()) {
            log.trace("%s: received VoteRequest from %s: term=%d, my term=%d, last_log_term=%d, last_log_index=%d",
                    local_addr, sender, term, raft.currentTerm(), last_log_term, last_log_index);
        }
        boolean send_vote_rsp = false;
        synchronized (this) {
            if (voteFor(sender)) {
                if (sameOrNewer(last_log_term, last_log_index))
                    send_vote_rsp = true;
                else {
                    log.trace("%s: dropped VoteRequest from %s as my log is more up-to-date", local_addr, sender);
                }
            } else
                log.trace("%s: already voted for %s in term %d; skipping vote", local_addr, sender, term);
        }
        return send_vote_rsp;
    }

    protected synchronized void handleVoteResponse(int term) {
        if (role == Role.Candidate && term == raft.current_term) {
            if (++current_votes >= raft.majority) {
                // we've got the majority: become leader
                log.trace("%s: collected %d votes (majority=%d) in term %d -> becoming leader",
                        local_addr, current_votes, raft.majority, term);
                changeRole(Role.Leader);
            }
        }
    }

    protected synchronized void handleElectionTimeout() {
        log.trace("%s: election timeout", local_addr);
        switch (role) {
            case Follower:
                changeRole(Role.Candidate);
                startElection();
                break;
            case Candidate:
                startElection();
                break;
        }
    }

    /**
     * Returns true if last_log_term >= my own last log term, or last_log_index >= my own index
     *
     * @param last_log_term
     * @param last_log_index
     * @return
     */
    protected boolean sameOrNewer(int last_log_term, int last_log_index) {
        int my_last_log_index;
        LogEntry entry = raft.log().get(my_last_log_index = raft.log().lastAppended());
        int my_last_log_term = entry != null ? entry.term : 0;
        int comp = Integer.compare(my_last_log_term, last_log_term);
        return comp <= 0 && (comp < 0 || Integer.compare(my_last_log_index, last_log_index) <= 0);
    }

    protected synchronized boolean heartbeatReceived(final boolean flag) {
        boolean retval = heartbeat_received;
        heartbeat_received = flag;
        return retval;
    }

    protected void sendHeartbeat(int term, Endpoint leader) {
        List<Endpoint> mbrs = new ArrayList<Endpoint>();
        //TODO
        for (String mbr : raft.members()) {
            String[] mbrArr = mbr.split(":");
            mbrs.add(new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1])));
        }
        mbrs.remove(local_addr);
        final HeartbeatRequest heartbeatRequest = new HeartbeatRequest(term, leader);
        for (final Endpoint endpoint : mbrs) {
            heartbeatThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    Cache.getElectionService(endpoint).heartbeat(heartbeatRequest);
                }
            });
        }
    }

    protected void sendVoteRequest(int term) {
        int last_log_index = raft.log().lastAppended();
        LogEntry entry = raft.log().get(last_log_index);
        int last_log_term = entry != null ? entry.term() : 0;
        final VoteRequest voteRequest = new VoteRequest(term, last_log_term, last_log_index);
        voteRequest.setCandidateId(local_addr);
        log.trace("%s: sending %s", local_addr, voteRequest);
        List<Endpoint> mbrs = new ArrayList<Endpoint>();
        //TODO
        for (String mbr : raft.members()) {
            String[] mbrArr = mbr.split(":");
            mbrs.add(new Endpoint(mbrArr[0], Integer.parseInt(mbrArr[1])));
        }
        mbrs.remove(local_addr);
        for (final Endpoint endpoint : mbrs) {
            voteThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    Cache.getElectionService(endpoint).vote(voteRequest, new RpcCallback<VoteResponse, Boolean>() {
                        @Override
                        public Boolean callback(VoteResponse voteResponse) {
                            try {
                                if (voteResponse != null && voteResponse.isResult()) {
                                    handleVoteResponse(voteResponse.term());
                                }
                                return true;
                            } catch (Exception e) {
                                //TODO
                                return false;
                            }
                        }
                    });
                }
            });
        }
    }

    protected void sendVoteResponse(Endpoint dest, int term, boolean voteFor, RpcCallback<VoteResponse, Boolean> callback) {
        VoteResponse voteResponse = new VoteResponse(term, voteFor);
        log.trace("%s: sending %s", local_addr, voteResponse);
        if (callback != null) {
            callback.callback(voteResponse);
        }
    }

    protected void changeRole(Role new_role) {
        if (role == new_role) {
            return;
        }
        if (role != Role.Leader && new_role == Role.Leader) {
            raft.leader(local_addr);
            // send a first heartbeat immediately after the election so other candidates step down
            sendHeartbeat(raft.currentTerm(), raft.leader());
            stopElectionTimer();
            startHeartbeatTimer();
        } else if (role == Role.Leader && new_role != Role.Leader) {
            stopHeartbeatTimer();
            startElectionTimer();
            raft.leader(null);
        }
        role = new_role;
        raft.changeRole(role);
    }

    protected void startElection() {
        int new_term = 0;

        synchronized (this) {
            new_term = raft.createNewTerm();
            voteFor(null);
            current_votes = 0;
            // Vote for self - return if I already voted for someone else
            if (!voteFor(local_addr))
                return;
            current_votes++; // vote for myself
        }

        sendVoteRequest(new_term); // Send VoteRequest message; responses are received asynchronously. If majority -> become leader
    }

    /**
     * Vote cast for a candidate in the current term
     */
    public synchronized String votedFor() {
        return voted_for != null ? voted_for.toString() : null;
    }

    protected boolean voteFor(final Endpoint addr) {
        if (addr == null) {
            voted_for = null;
            return true;
        }
        if (voted_for == null) {
            voted_for = addr;
            return true;
        }
        return voted_for.equals(addr); // a vote for the same candidate in the same term is ok
    }

    protected void startElectionTimer() {
        if (!no_elections && (election_task == null || election_task.isDone())) {
            election_task = timer.scheduleWithDynamicInterval(new ElectionTask());
        }
    }

    protected void stopElectionTimer() {
        if (election_task != null) {
            election_task.cancel(true);
        }
    }

    protected void startHeartbeatTimer() {
        if (heartbeat_task == null || heartbeat_task.isDone()) {
            heartbeat_task = timer.scheduleAtFixedRate(new HeartbeatTask(), heartbeat_interval, heartbeat_interval, TimeUnit.MILLISECONDS);
        }
    }

    protected void stopHeartbeatTimer() {
        if (heartbeat_task != null) {
            heartbeat_task.cancel(true);
        }
    }

    protected class ElectionTask implements TimeScheduler.Task {

        public long nextInterval() {
            return computeElectionTimeout(election_min_interval, election_max_interval);
        }

        public void run() {
            if (!heartbeatReceived(false)) {
                handleElectionTimeout();
            }
        }

        protected long computeElectionTimeout(long min, long max) {
            long diff = max - min;
            return (int) ((Math.random() * 100000) % diff) + min;
        }

    }

    protected class HeartbeatTask implements Runnable {

        public void run() {
            sendHeartbeat(raft.currentTerm(), raft.leader());
        }

    }

}