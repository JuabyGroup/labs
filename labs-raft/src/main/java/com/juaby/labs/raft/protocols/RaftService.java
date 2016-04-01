package com.juaby.labs.raft.protocols;

/**
 * Created by juaby on 16-3-24.
 */
public interface RaftService {

    public AppendEntriesResponse appendEntries(AppendEntriesRequest appendEntriesRequest);

    public AppendEntriesResponse installSnapshot(InstallSnapshotRequest installSnapshotRequest);

}
