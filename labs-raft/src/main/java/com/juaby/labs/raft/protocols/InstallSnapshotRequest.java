package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * @author Bela Ban
 * @since 0.1
 */
public class InstallSnapshotRequest extends RaftHeader {

    protected Endpoint leader;
    protected int last_included_index;
    protected int last_included_term;

    public InstallSnapshotRequest() {
    }

    public InstallSnapshotRequest(int term) {
        super(term);
    }

    public InstallSnapshotRequest(int term, Endpoint leader, int last_included_index, int last_included_term) {
        this(term);
        this.leader = leader;
        this.last_included_index = last_included_index;
        this.last_included_term = last_included_term;
    }

    public Endpoint getLeader() {
        return leader;
    }

    public void setLeader(Endpoint leader) {
        this.leader = leader;
    }

    public int getLast_included_index() {
        return last_included_index;
    }

    public void setLast_included_index(int last_included_index) {
        this.last_included_index = last_included_index;
    }

    public int getLast_included_term() {
        return last_included_term;
    }

    public void setLast_included_term(int last_included_term) {
        this.last_included_term = last_included_term;
    }

    @Override
    public String toString() {
        return super.toString() + ", leader=" + leader + ", last_included_index=" + last_included_index +
                ", last_included_term=" + last_included_term;
    }

}