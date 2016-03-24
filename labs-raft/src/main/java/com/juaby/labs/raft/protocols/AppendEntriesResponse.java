package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

/**
 * @author Bela Ban
 * @since 0.1
 */
public class AppendEntriesResponse extends RaftHeader {

    protected AppendResult result;

    protected Endpoint src;

    public AppendEntriesResponse() {
    }

    public AppendEntriesResponse(int term, AppendResult result) {
        super(term);
        this.result = result;
    }

    public AppendResult getResult() {
        return result;
    }

    public void setResult(AppendResult result) {
        this.result = result;
    }

    public Endpoint getSrc() {
        return src;
    }

    public void setSrc(Endpoint src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return super.toString() + ", result: " + result;
    }

}
