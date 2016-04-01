package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.rpc.util.RpcListener;

/**
 * Created by juaby on 16-3-30.
 */
public class AppendResponseListener implements RpcListener {

    private RaftProtocol raftProtocol;

    public AppendResponseListener() {
    }

    public AppendResponseListener(RaftProtocol raftProtocol) {
        this.raftProtocol = raftProtocol;
    }

    @Override
    public void handle(Object o) {
        if (o != null) {
            AppendEntriesResponse response = (AppendEntriesResponse) o;
            Cache.getRaftProtocol().getRaftImpl().handleAppendEntriesResponse(response.getSrc(), response.term(), response.getResult());
        }
    }

    @Override
    public void catchException(Throwable throwable) {

    }

}