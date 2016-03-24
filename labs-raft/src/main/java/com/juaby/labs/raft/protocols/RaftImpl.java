package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * Base class for the different roles a RAFT node can have (follower, candidate, leader)
 *
 * @author Bela Ban
 * @since 0.1
 */
public abstract class RaftImpl {

    protected RaftProtocol raft; // a ref to the enclosing RAFT protocol

    public RaftImpl(RaftProtocol raft) {
        this.raft = raft;
    }

    public RaftProtocol raft() {
        return raft;
    }

    public RaftImpl raft(RaftProtocol r) {
        this.raft = r;
        return this;
    }

    /**
     * Called right after instantiation
     */
    public void init() {
    }

    /**
     * Called before getting destroyed (on a role change)
     */
    public void destroy() {
    }


    /**
     * Called (on a follower) when an AppendEntries request is received
     *
     * @param data           The data (command to be appended to the log)
     * @param offset         The offset
     * @param length         The length
     * @param leader         The leader's address (= the sender)
     * @param prev_log_index The index of the previous log entry
     * @param prev_log_term  The term of the previous log entry
     * @param entry_term     The term of the entry
     * @param leader_commit  The leader's commit_index
     * @param internal       True if the command is an internal command
     * @return AppendResult A result (true or false), or null if the request was ignored (e.g. due to lower term)
     */
    protected AppendResult handleAppendEntriesRequest(byte[] data, int offset, int length, Endpoint leader,
                                                      int prev_log_index, int prev_log_term, int entry_term,
                                                      int leader_commit, boolean internal) {
        // todo: synchronize
        raft.leader(leader);

        if (data == null || length == 0) { // we got an empty AppendEntries message containing only leader_commit
            return handleCommitRequest(leader, leader_commit);
        }

        LogEntry prev = raft.log_impl.get(prev_log_index);
        int curr_index = prev_log_index + 1;

        if (prev == null && prev_log_index > 0) { // didn't find entry
            return new AppendResult(false, raft.lastAppended());
        }

        // if the term at prev_log_index != prev_term -> return false and the start index of the conflicting term
        if (prev_log_index == 0 || prev.term == prev_log_term) {
            LogEntry existing = raft.log_impl.get(curr_index);
            if (existing != null && existing.term != entry_term) {
                // delete this and all subsequent entries and overwrite with received entry
                raft.deleteAllLogEntriesStartingFrom(curr_index);
            }
            raft.append(entry_term, curr_index, data, offset, length, internal).commitLogTo(leader_commit);
            if (internal) {
                raft.executeInternalCommand(null, data, offset, length);
            }
            return new AppendResult(true, curr_index).commitIndex(raft.commitIndex());
        }
        return new AppendResult(false, getFirstIndexOfConflictingTerm(prev_log_index, prev.term), prev.term);
    }


    protected void handleAppendEntriesResponse(Endpoint sender, int term, AppendResult result) {
    }

    protected AppendEntriesResponse handleInstallSnapshotRequest(InstallSnapshotRequest installSnapshotRequest) {
        return null;
    }

    /**
     * Finds the first index at which conflicting_term starts, going back from start_index towards the head of the log
     */
    protected int getFirstIndexOfConflictingTerm(int start_index, int conflicting_term) {
        Log log = raft.log_impl;
        int first = Math.max(1, log.firstAppended()), last = log.lastAppended();
        int retval = Math.min(start_index, last);
        for (int i = retval; i >= first; i--) {
            LogEntry entry = log.get(i);
            if (entry == null || entry.term != conflicting_term) {
                break;
            }
            retval = i;
        }
        return retval;
    }

    protected AppendResult handleCommitRequest(Endpoint sender, int leader_commit) {
        raft.commitLogTo(leader_commit);
        return new AppendResult(true, raft.lastAppended()).commitIndex(raft.commitIndex());
    }

}