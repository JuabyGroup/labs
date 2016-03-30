package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.RpcCallback;

/**
 * Created by juaby on 16-3-24.
 */
public interface RaftService {

    public AppendEntriesResponse appendEntries(AppendEntriesRequest appendEntriesRequest);

    public AppendEntriesResponse appendEntries(AppendEntriesRequest appendEntriesRequest,
                                                           RpcCallback<AppendEntriesResponse, Boolean> callback);

    public AppendEntriesResponse installSnapshot(InstallSnapshotRequest installSnapshotRequest);

}
