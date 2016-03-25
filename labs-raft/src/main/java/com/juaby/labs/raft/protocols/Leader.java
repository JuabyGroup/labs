package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.RequestTable;
import com.juaby.labs.rpc.util.Endpoint;

/**
 * Implements the behavior of a RAFT leader
 *
 * @author Bela Ban
 * @since 0.1
 */
public class Leader extends RaftImpl {

    public Leader(RaftProtocol raft) {
        super(raft);
    }

    public void init() {
        super.init();
        raft.createRequestTable();
        raft.createCommitTable();
        raft.startResendTask();
    }

    public void destroy() {
        super.destroy();
        raft.stopResendTask();
        raft.request_table = null;
        raft.commit_table = null;
    }

    @Override
    protected void handleAppendEntriesResponse(Endpoint sender, int term, AppendResult result) {
        RequestTable<String> reqtab = raft.request_table;
        if (reqtab == null) {
            throw new IllegalStateException("request table cannot be null in leader");
        }
        String sender_raft_id = raft.raftId();
        raft.getLog().trace("%s: received AppendEntries response from %s for term %d: %s", raft.local_addr, sender, term, result);
        if (result.success) {
            raft.commit_table.update(sender, result.getIndex(), result.getIndex() + 1, result.commit_index, false);
            if (reqtab.add(result.index, sender_raft_id, raft.majority())) {
                raft.handleCommit(result.index);
            }
        } else {
            raft.commit_table.update(sender, 0, result.getIndex(), result.commit_index, true);
        }
    }

}