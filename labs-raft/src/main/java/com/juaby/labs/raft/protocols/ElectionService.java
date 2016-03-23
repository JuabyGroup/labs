package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.RpcCallback;

/**
 * Created by juaby on 16-3-22.
 */
public interface ElectionService {

    public void vote(VoteRequest voteRequest, RpcCallback<VoteResponse, Boolean> callback);

    public void heartbeat(HeartbeatRequest heartbeatRequest);

}
