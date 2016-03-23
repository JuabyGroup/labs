package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * Used by {@link ElectionProtocol} to send heartbeats. Contrary to the RAFT paper, heartbeats are
 * not emulated with AppendEntriesRequests
 *
 * @author Bela Ban
 * @since 0.1
 */
public class HeartbeatRequest extends RaftHeader {

    protected Endpoint leader;

    public HeartbeatRequest() {
    }

    public HeartbeatRequest(int term, Endpoint leader) {
        super(term);
        this.leader = leader;
    }

    public Endpoint getLeader() {
        return leader;
    }

    public void setLeader(Endpoint leader) {
        this.leader = leader;
    }

    public String toString() {
        return super.toString() + ", leader=" + leader;
    }

}
