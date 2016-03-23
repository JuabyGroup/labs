package com.juaby.labs.raft.protocols;

/**
 * @author Bela Ban
 * @since 0.1
 */
public class VoteResponse extends RaftHeader {

    protected boolean result;

    public VoteResponse() {
    }

    public VoteResponse(int term, boolean result) {
        super(term);
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String toString() {
        return super.toString() + ", result=" + result;
    }

}
