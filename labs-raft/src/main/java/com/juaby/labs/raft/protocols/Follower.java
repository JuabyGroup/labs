package com.juaby.labs.raft.protocols;

import com.juaby.labs.raft.util.ByteArrayDataInputStream;
import com.juaby.labs.raft.util.Util;
import com.juaby.labs.rpc.util.Endpoint;

/**
 * Implements the behavior of a RAFT follower
 *
 * @author Bela Ban
 * @since 0.1
 */
public class Follower extends RaftImpl {

    public Follower(RaftProtocol raft) {
        super(raft);
    }

    @Override
    protected AppendEntriesResponse handleInstallSnapshotRequest(InstallSnapshotRequest request) {
        int term = request.term();
        Endpoint leader = request.getLeader();
        int last_included_index = request.getLast_included_index();
        int last_included_term = request.getLast_included_term();
        byte[] data = request.getData();

        // 1. read the state (in the message's buffer) and apply it to the state machine (clear the SM before?)

        // 2. Delete the log (if it exists) and create a new log. Append a dummy entry at last_included_index with an
        //    empty buffer and term=last_included_term
        //    - first_appended=last_appended=commit_index=last_included_index

        StateMachine sm;
        if ((sm = raft.state_machine) == null) {
            raft.getLog().error("%s: no state machine set, cannot install snapshot", raft.local_addr);
            return new AppendEntriesResponse(raft.currentTerm(), null);
        }
        try {
            ByteArrayDataInputStream in = new ByteArrayDataInputStream(data, request.getOffset(), data.length);
            sm.readContentFrom(in);

            raft.doSnapshot();

            // insert a dummy entry
            Log log = raft.log();
            log.append(last_included_index, true, new LogEntry(last_included_term, null));
            raft.last_appended = last_included_index;
            log.commitIndex(last_included_index);
            raft.commit_index = last_included_index;
            log.truncate(last_included_index);

            raft.getLog().debug("%s: applied snapshot (%s) from %s; last_appended=%d, commit_index=%d",
                    raft.local_addr, Util.printBytes(data.length), leader, raft.lastAppended(), raft.commitIndex());

            AppendResult result = new AppendResult(true, last_included_index).commitIndex(raft.commitIndex());
            AppendEntriesResponse response = new AppendEntriesResponse(raft.currentTerm(), result);
            response.setSrc(raft.local_addr);
            return response;
        } catch (Exception ex) {
            raft.getLog().error("%s: failed applying snapshot from %s: %s", raft.local_addr, leader, ex);
            return new AppendEntriesResponse(raft.currentTerm(), null);
        }
    }

}