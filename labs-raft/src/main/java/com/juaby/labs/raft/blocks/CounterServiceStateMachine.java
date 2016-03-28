package com.juaby.labs.raft.blocks;

import com.juaby.labs.raft.RaftHandle;
import com.juaby.labs.raft.protocols.*;
import com.juaby.labs.rpc.util.SerializeTool;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides a consensus based distributed counter (similar to AtomicLong) which can be atomically updated across a cluster.
 *
 * @author Bela Ban
 * @since 0.2
 */
public class CounterServiceStateMachine implements StateMachine, RaftProtocol.RoleChange {

    protected RaftHandle raft;

    protected long repl_timeout = 20000; // timeout (ms) to wait for a majority to ack a write

    /**
     * If true, reads can return the local counter value directly. Else, reads have to go through the leader
     */
    protected boolean allow_dirty_reads = true;

    // keys: counter names, values: counter values
    protected final Map<String, Long> counters = new HashMap<>();

    protected enum CounterCommandType {create, delete, get, set, compareAndSet, incrementAndGet, decrementAndGet, addAndGet}

    public CounterServiceStateMachine() {
        this.raft = new RaftHandle(this);
        raft.addRoleListener(this);
    }

    public void addRoleChangeListener(RaftProtocol.RoleChange listener) {
        raft.addRoleListener(listener);
    }

    public long replTimeout() {
        return repl_timeout;
    }

    public CounterServiceStateMachine replTimeout(long timeout) {
        this.repl_timeout = timeout;
        return this;
    }

    public boolean allowDirtyReads() {
        return allow_dirty_reads;
    }

    public CounterServiceStateMachine allowDirtyReads(boolean flag) {
        allow_dirty_reads = flag;
        return this;
    }

    public int lastApplied() {
        return raft.lastApplied();
    }

    public int commitIndex() {
        return raft.commitIndex();
    }

    public void snapshot() throws Exception {
        raft.snapshot();
    }

    public int logSize() {
        return raft.logSizeInBytes();
    }

    public String raftId() {
        return raft.raftId();
    }

    public CounterServiceStateMachine raftId(String id) {
        raft.raftId(id);
        return this;
    }

    /**
     * Returns an existing counter, or creates a new one if none exists
     *
     * @param name          Name of the counter, different counters have to have different names
     * @param initial_value The initial value of a new counter if there is no existing counter. Ignored
     *                      if the counter already exists
     * @return The counter implementation
     */
    public Counter getOrCreateCounter(String name, long initial_value) throws Exception {
        Object existing_value = allow_dirty_reads ? _get(name) : invoke(CounterCommandType.get, name, false);
        if (existing_value != null) {
            counters.put(name, (Long) existing_value);
        } else {
            Object retval = invoke(CounterCommandType.create, name, false, initial_value);
            if (retval instanceof Long) {
                counters.put(name, (Long) retval);
            }
        }
        return new CounterImpl(name, this);
    }

    /**
     * Deletes a counter instance (on the coordinator)
     *
     * @param name The name of the counter. No-op if the counter doesn't exist
     */
    public void deleteCounter(String name) throws Exception {
        invoke(CounterCommandType.delete, name, true);
    }

    public String printCounters() {
        return counters.entrySet()
                .stream().collect(StringBuilder::new,
                        (sb, entry) -> sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n"),
                        (l, r) -> {
                        }).toString();
    }

    public long get(String name) throws Exception {
        Object retval = allow_dirty_reads ? _get(name) : invoke(CounterCommandType.get, name, false);
        return (long) retval;
    }

    public void set(String name, long new_value) throws Exception {
        invoke(CounterCommandType.set, name, true, new_value);
    }

    public boolean compareAndSet(String name, long expect, long update) throws Exception {
        Object retval = invoke(CounterCommandType.compareAndSet, name, false, expect, update);
        return (boolean) retval;
    }

    public long incrementAndGet(String name) throws Exception {
        Object retval = invoke(CounterCommandType.incrementAndGet, name, false);
        return (long) retval;
    }

    public long decrementAndGet(String name) throws Exception {
        Object retval = invoke(CounterCommandType.decrementAndGet, name, false);
        return (long) retval;
    }

    public long addAndGet(String name, long delta) throws Exception {
        Object retval = invoke(CounterCommandType.addAndGet, name, false, delta);
        return (long) retval;
    }

    @Override
    public byte[] apply(byte[] data, int offset, int length) throws Exception {
        Command<CounterCommandType, KeyValueWapper<String, Long[]>> command = new Command<CounterCommandType, KeyValueWapper<String, Long[]>>();
        SerializeTool.deserialize(data, command);
        CounterCommandType type = command.getType();
        String name = command.getData().getKey();
        long v1, v2, retval;
        switch (type) {
            case create:
                v1 = command.getData().getValue()[0];
                retval = _create(name, v1);
                return SerializeTool.serialize(Long.valueOf(retval));
            case delete:
                _delete(name);
                break;
            case get:
                retval = _get(name);
                return SerializeTool.serialize(Long.valueOf(retval));
            case set:
                v1 = command.getData().getValue()[0];
                _set(name, v1);
                break;
            case compareAndSet:
                v1 = command.getData().getValue()[0];
                v2 = command.getData().getValue()[1];
                boolean success = _cas(name, v1, v2);
                return SerializeTool.serialize(Boolean.valueOf(success));
            case incrementAndGet:
                retval = _add(name, +1L);
                return SerializeTool.serialize(Long.valueOf(retval));
            case decrementAndGet:
                retval = _add(name, -1L);
                return SerializeTool.serialize(Long.valueOf(retval));
            case addAndGet:
                v1 = command.getData().getValue()[0];
                retval = _add(name, v1);
                return SerializeTool.serialize(Long.valueOf(retval));
            default:
                throw new IllegalArgumentException("command " + command + " is unknown");
        }
        return null;
    }

    @Override
    public void writeContentTo(DataOutput out) throws Exception {
        synchronized (counters) {
            int size = counters.size();
            out.writeInt(size);
            for (Map.Entry<String, Long> entry : counters.entrySet()) {
                KeyValueWapper<String, Long> keyValueWapper = new KeyValueWapper<String, Long>(entry.getKey(), entry.getValue());
                byte[] keyValueBuf = SerializeTool.serialize(keyValueWapper);
                out.writeInt(keyValueBuf.length);
                out.write(keyValueBuf);
            }
        }
    }

    @Override
    public void readContentFrom(DataInput in) throws Exception {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int keyValueLength = in.readInt();
            byte[] keyValueBuf = new byte[keyValueLength];
            in.readFully(keyValueBuf);
            KeyValueWapper<String, Long> keyValueWapper = new KeyValueWapper<String, Long>();
            SerializeTool.deserialize(keyValueBuf, keyValueWapper);
            String name = keyValueWapper.getKey();
            Long value = keyValueWapper.getValue();
            counters.put(name, value);
        }
    }

    public void dumpLog() {
        raft.logEntries((entry, index) -> {
            StringBuilder sb = new StringBuilder().append(index).append(" (").append(entry.term()).append("): ");
            if (entry.command() == null) {
                sb.append("<marker record>");
                System.out.println(sb);
                return;
            }
            if (entry.internal()) {
                try {
                    InternalCommand cmd = new InternalCommand();
                    SerializeTool.deserialize(entry.getCommand(), cmd);
                    sb.append("[internal] ").append(cmd);
                } catch (Exception ex) {
                    sb.append("[failure reading internal cmd] ").append(ex);
                }
                System.out.println(sb);
                return;
            }
            try {
                Command<CounterCommandType, KeyValueWapper<String, Long[]>> command = new Command<CounterCommandType, KeyValueWapper<String, Long[]>>();
                SerializeTool.deserialize(entry.getCommand(), command);
                CounterCommandType type = command.getType();
                String name = command.getData().getKey();
                switch (type) {
                    case create:
                    case set:
                    case addAndGet:
                        sb.append(print(type, name, command));
                        break;
                    case delete:
                    case get:
                    case incrementAndGet:
                    case decrementAndGet:
                        sb.append(print(type, name, command));
                        break;
                    case compareAndSet:
                        sb.append(print(type, name, command));
                        break;
                    default:
                        throw new IllegalArgumentException("command " + type + " is unknown");
                }
            } catch (Throwable t) {
                sb.append(t);
            }
            System.out.println(sb);
        });
    }

    @Override
    public void roleChanged(Role role) {
        System.out.println("-- changed role to " + role);
    }

    protected Object invoke(CounterCommandType type, String name, boolean ignore_return_value, long... values) throws Exception {
        byte[] cmd;
        try {
            Command<CounterCommandType, KeyValueWapper<String, Long[]>> command = null;
            Long[] valueArr = new Long[values.length];
            for (int i = 0; i < values.length; i++) {
                valueArr[i] = values[i];
            }
            command = new Command<CounterCommandType, KeyValueWapper<String, Long[]>>(type, new KeyValueWapper<String, Long[]>(name, valueArr));
            cmd = SerializeTool.serialize(command);
        } catch (Exception ex) {
            throw new Exception("serialization failure (cmd=" + type + ", name=" + name + ")");
        }

        byte[] rsp = raft.set(cmd, 0, cmd.length, repl_timeout, TimeUnit.MILLISECONDS);
        if (CounterCommandType.set == type) {
            return null;
        }
        if (CounterCommandType.compareAndSet == type) {
            Boolean result = true;
            return ignore_return_value ? null : SerializeTool.deserialize(rsp, result);
        }
        Long rval = 0L;
        return ignore_return_value ? null : (rsp != null ? SerializeTool.deserialize(rsp, rval) : null);
    }

    protected static String print(CounterCommandType type, String name, Command<CounterCommandType, KeyValueWapper<String, Long[]>> command) {
        StringBuilder sb = new StringBuilder(type.toString()).append("(").append(name);
        Long[] values = command.getData().getValue();
        for (int i = 0; i < values.length; i++) {
            long val = values[i];
            sb.append(", ").append(val);
        }
        sb.append(")");
        return sb.toString();
    }

    protected long _create(String name, long initial_value) {
        synchronized (counters) {
            Long val = counters.get(name);
            if (val != null)
                return val;
            counters.put(name, initial_value);
            return initial_value;
        }
    }

    protected void _delete(String name) {
        synchronized (counters) {
            counters.remove(name);
        }
    }

    protected long _get(String name) {
        synchronized (counters) {
            Long retval = counters.get(name);
            return retval != null ? (long) retval : 0;
        }
    }

    protected void _set(String name, long new_val) {
        synchronized (counters) {
            counters.put(name, new_val);
        }
    }

    protected boolean _cas(String name, long expected, long value) {
        synchronized (counters) {
            Long existing_value = counters.get(name);
            if (existing_value == null) return false;
            if (existing_value == expected) {
                counters.put(name, value);
                return true;
            }
            return false;
        }
    }

    protected long _add(String name, long delta) {
        synchronized (counters) {
            Long val = counters.get(name);
            if (val == null)
                val = (long) 0;
            counters.put(name, val + delta);
            return val + delta;
        }
    }

}