package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.store.LogEntry;

/**
 * Created by juaby on 16-3-24.
 */
public class RaftServiceImpl implements RaftService {

    private RaftProtocol raftProtocol;

    public RaftServiceImpl(RaftProtocol raftProtocol) {
        this.raftProtocol = raftProtocol;
    }

    @Override
    public AppendEntriesResponse appendEntries(AppendEntriesRequest request) {
        // if hdr.term < current_term -> drop message
        // if hdr.term > current_term -> set current_term and become Follower, accept message
        // if hdr.term == current_term -> accept message
        if (raftProtocol.currentTerm(request.term) < 0) {
            return new AppendEntriesResponse(raftProtocol.currentTerm(), new AppendResult(false));
        }
        LogEntry[] entries = request.getEntries();
        LogEntry entry = null;
        if (entries != null && entries.length >= 1) {
            entry = entries[0];
        }
        if (entry != null) {
            AppendResult result = raftProtocol.getRaftImpl().handleAppendEntriesRequest(entry.getCommand(), entry.getOffset(), entry.getLength(), request.getLeader(),
                    request.prev_log_index, request.prev_log_term, request.entry_term,
                    request.leader_commit, request.internal);

            if (result != null) {
                if (entry.getCommand() == null || entry.getLength() == 0) { // we got an empty AppendEntries message containing only leader_commit
                    raftProtocol.commitLogTo(request.leader_commit);
                }

                result.commitIndex(raftProtocol.commitIndex());
                AppendEntriesResponse response = new AppendEntriesResponse(raftProtocol.currentTerm(), result);
                response.setSrc(raftProtocol.local_addr);
                return response;
            }
        }
        return new AppendEntriesResponse(raftProtocol.currentTerm(), new AppendResult(false));
    }

    @Override
    public AppendEntriesResponse installSnapshot(InstallSnapshotRequest request) {
        // if hdr.term < current_term -> drop message
        // if hdr.term > current_term -> set current_term and become Follower, accept message
        // if hdr.term == current_term -> accept message
        if (raftProtocol.currentTerm(request.term) < 0) {
            return new AppendEntriesResponse(raftProtocol.currentTerm(), null);
        }
        return raftProtocol.getRaftImpl().handleInstallSnapshotRequest(request);
    }

}