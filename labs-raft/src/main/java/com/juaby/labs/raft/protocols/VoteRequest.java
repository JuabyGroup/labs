package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * @author Bela Ban
 * @since 0.1
 */
public class VoteRequest extends RaftHeader {

    protected int last_log_term;
    protected int last_log_index;
    protected Endpoint candidateId;

    public VoteRequest() {
    }

    public VoteRequest(int term, int last_log_term, int last_log_index) {
        super(term);
        this.last_log_term = last_log_term;
        this.last_log_index = last_log_index;
    }

    public int lastLogTerm() {
        return last_log_term;
    }

    public int lastLogIndex() {
        return last_log_index;
    }

    public void lastLogTerm(int last_log_term) {
        this.last_log_term = last_log_term;
    }

    public void lastLogIndex(int last_log_index) {
        this.last_log_index = last_log_index;
    }

    public Endpoint getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Endpoint candidateId) {
        this.candidateId = candidateId;
    }

    public String toString() {
        return super.toString() + ", last_log_term=" + last_log_term + ", last_log_index=" + last_log_index;
    }

}