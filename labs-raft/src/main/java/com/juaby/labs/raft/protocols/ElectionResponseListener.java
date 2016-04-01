package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.rpc.util.RpcListener;

/**
 * Created by juaby on 16-3-30.
 */
public class ElectionResponseListener implements RpcListener {

    private ElectionProtocol electionProtocol;

    public ElectionResponseListener() {
    }

    public ElectionResponseListener(ElectionProtocol raftProtocol) {
        this.electionProtocol = electionProtocol;
    }

    @Override
    public void handle(Object o) {
        if (o != null) {
            VoteResponse response = (VoteResponse) o;
            if (response.result()) {
                Cache.getElectionProtocol().handleVoteResponse(response.term());
            }
        }
    }

    @Override
    public void catchException(Throwable throwable) {

    }

}