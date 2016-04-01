package com.juaby.labs.raft.protocols;

/**
 * Election
 *
 * Created by juaby on 16-3-22.
 */
public interface ElectionService {

    public VoteResponse vote(VoteRequest voteRequest);

    public void heartbeat(HeartbeatRequest heartbeatRequest);

}
