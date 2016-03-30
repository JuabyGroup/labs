package com.juaby.labs.raft;

import com.juaby.labs.raft.protocols.*;
import com.juaby.labs.raft.store.Log;
import com.juaby.labs.raft.store.LogEntry;
import com.juaby.labs.raft.util.Cache;
import com.juaby.labs.rpc.util.Endpoint;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.ObjIntConsumer;

/**
 * Main interaction point for applications with jgroups-raft. Provides methods to make changes, register a state machine,
 * get commit-index and last-applied, register {@link RaftProtocol.RoleChange} listeners etc<p/>
 * Sample use:
 * <pre>
 *     JChannel ch=createChannel();
 *     RaftHandle handle=new RaftHandle(ch, new StateMachineImpl()); // implements StateMachine
 *     handle.addRoleListener(this);
 *     handle.setAsync(buf, 0, buf.length).whenComplete((buf,ex) -> ...);
 * </pre>
 *
 * @author Bela Ban
 * @since 0.2
 */
public class RaftHandle implements Settable {

    protected RaftProtocol raft;
    protected Settable settable; // usually REDIRECT (at the top of the stack)

    /**
     * Creates a RaftHandle instance.
     *
     * @param sm An implementation of {@link StateMachine}. Can be null, ie. if it is set later via {@link #stateMachine(StateMachine)}.
     */
    public RaftHandle(StateMachine sm) {
        raft = Cache.getRaftProtocol();
        settable = raft;
        stateMachine(sm);
    }

    public RaftProtocol raft() {
        return raft;
    }

    public String raftId() {
        return raft.raftId();
    }

    public RaftHandle raftId(String id) {
        raft.raftId(id);
        return this;
    }

    public Endpoint leader() {
        return raft.leader();
    }

    public boolean isLeader() {
        return raft.isLeader();
    }

    public StateMachine stateMachine() {
        return raft.stateMachine();
    }

    public RaftHandle stateMachine(StateMachine sm) {
        raft.stateMachine(sm);
        return this;
    }

    public RaftHandle addRoleListener(RaftProtocol.RoleChange listener) {
        raft.addRoleListener(listener);
        return this;
    }

    public RaftHandle removeRoleListener(RaftProtocol.RoleChange listener) {
        raft.remRoleListener(listener);
        return this;
    }

    public int currentTerm() {
        return raft.currentTerm();
    }

    public int lastApplied() {
        return raft.lastAppended();
    }

    public int commitIndex() {
        return raft.commitIndex();
    }

    public void snapshot() throws Exception {
        raft.snapshot();
    }

    public Log log() {
        return raft.log();
    }

    public int logSize() {
        return raft.logSize();
    }

    public int logSizeInBytes() {
        return raft.logSizeInBytes();
    }

    public void logEntries(ObjIntConsumer<LogEntry> func) {
        raft.logEntries(func);
    }

    @Override
    public byte[] set(byte[] buf, int offset, int length) throws Exception {
        return settable.set(buf, offset, length);
    }

    @Override
    public byte[] set(byte[] buf, int offset, int length, long timeout, TimeUnit unit) throws Exception {
        return settable.set(buf, offset, length, timeout, unit);
    }

    @Override
    public CompletableFuture<byte[]> setAsync(byte[] buf, int offset, int length) {
        return settable.setAsync(buf, offset, length);
    }

}