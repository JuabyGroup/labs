package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.store.LogEntry;
import com.juaby.labs.rpc.util.Endpoint;

/**
 * Used to send AppendEntries messages to cluster members. The log entries are contained in actual payload of the message,
 * not in this header.
 *
 * @author Bela Ban
 * @since 0.1
 */
public class AppendEntriesRequest extends RaftHeader {

    protected Endpoint leader;         // probably not needed as msg.src() contains the leader's address already

    protected int prev_log_index;

    protected int prev_log_term;

    protected int entry_term;     // term of the given entry, e.g. when relaying a log to a late joiner

    protected LogEntry[] entries;

    protected int leader_commit;  // the commit_index of the leader

    protected boolean internal;

    public AppendEntriesRequest() {
    }

    public AppendEntriesRequest(int term, Endpoint leader, int prev_log_index, int prev_log_term, int entry_term,
                                int leader_commit, boolean internal) {
        super(term);
        this.leader = leader;
        this.prev_log_index = prev_log_index;
        this.prev_log_term = prev_log_term;
        this.entry_term = entry_term;
        this.leader_commit = leader_commit;
        this.internal = internal;
    }

    public Endpoint getLeader() {
        return leader;
    }

    public void setLeader(Endpoint leader) {
        this.leader = leader;
    }

    public int prevLogIndex() {
        return prev_log_index;
    }

    public void prevLogIndex(int prev_log_index) {
        this.prev_log_index = prev_log_index;
    }

    public int prevLogTerm() {
        return prev_log_term;
    }

    public void prevLogTerm(int prev_log_term) {
        this.prev_log_term = prev_log_term;
    }

    public int entryTerm() {
        return entry_term;
    }

    public void entryTerm(int entry_term) {
        this.entry_term = entry_term;
    }

    public LogEntry[] getEntries() {
        return entries;
    }

    public void setEntries(LogEntry[] entries) {
        this.entries = entries;
    }

    public int leaderCommit() {
        return leader_commit;
    }

    public void leaderCommit(int leader_commit) {
        this.leader_commit = leader_commit;
    }

    public boolean internal() {
        return internal;
    }

    public void internal(boolean internal) {
        this.internal = internal;
    }

    @Override
    public String toString() {
        return super.toString() + ", leader=" + leader + ", prev_log_index=" + prev_log_index +
                ", prev_log_term=" + prev_log_term + ", entry_term=" + entry_term + ", leader_commit=" + leader_commit +
                ", internal=" + internal;
    }

}
