package com.juaby.labs.raft.protocols;

/**
 * @author Bela Ban
 * @since 0.1
 */
public abstract class RaftHeader {

    protected int term;

    public RaftHeader() {
    }

    public RaftHeader(int term) {
        this.term = term;
    }

    public int term() {
        return term;
    }

    public RaftHeader term(int t) {
        term = t;
        return this;
    }

    public String toString() {
        return getClass().getSimpleName() + ": term=" + term;
    }

}
