package com.juaby.labs.raft.blocks;

import com.juaby.labs.raft.RaftHandle;
import com.juaby.labs.raft.protocols.*;
import com.juaby.labs.rpc.util.SerializeTool;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A key-value store replicating its contents with RAFT via consensus
 *
 * @author Bela Ban
 * @since 0.1
 */
public class KVReplicatedStateMachine<K, V> implements StateMachine {

    protected RaftHandle raft;
    protected long repl_timeout = 20000; // timeout (ms) to wait for a majority to ack a write
    protected final List<Notification> listeners = new ArrayList<>();

    // Hashmap for the contents. Doesn't need to be reentrant, as updates will be applied sequentially
    protected final Map<K, V> map = new HashMap<>();

    protected enum KVCommandType {
        PUT, REMOVE
    }

    public KVReplicatedStateMachine() {
        this.raft = new RaftHandle(this);
    }

    public KVReplicatedStateMachine timeout(long timeout) {
        this.repl_timeout = timeout;
        return this;
    }

    public void addRoleChangeListener(RaftProtocol.RoleChange listener) {
        raft.addRoleListener(listener);
    }

    public void addNotificationListener(Notification n) {
        if (n != null) listeners.add(n);
    }

    public void removeNotificationListener(Notification n) {
        listeners.remove(n);
    }

    public void removeRoleChangeListener(RaftProtocol.RoleChange listener) {
        raft.removeRoleListener(listener);
    }

    public int lastApplied() {
        return raft.lastApplied();
    }

    public int commitIndex() {
        return raft.commitIndex();
    }

    public void snapshot() throws Exception {
        if (raft != null) raft.snapshot();
    }

    public int logSize() {
        return raft != null ? raft.logSizeInBytes() : 0;
    }

    public String raftId() {
        return raft.raftId();
    }

    public KVReplicatedStateMachine<K, V> raftId(String id) {
        raft.raftId(id);
        return this;
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
                    SerializeTool.deserialize(entry.command(), cmd);
                    sb.append("[internal] ").append(cmd).append("\n");
                } catch (Exception ex) {
                    sb.append("[failure reading internal cmd] ").append(ex).append("\n");
                }
                System.out.println(sb);
                return;
            }
            try {
                Command<KVCommandType, KeyValueWapper<K, V>> command = new Command<KVCommandType, KeyValueWapper<K, V>>();
                SerializeTool.deserialize(entry.command(), command);
                KVCommandType type = command.getType();
                K key = command.getData().getKey();
                switch (type) {
                    case PUT:
                        V val = command.getData().getValue();
                        sb.append("put(").append(key).append(", ").append(val).append(")");
                        break;
                    case REMOVE:
                        sb.append("remove(").append(key).append(")");
                        break;
                    default:
                        sb.append("type " + type + " is unknown");
                }
            } catch (Throwable t) {
                sb.append(t);
            }
            System.out.println(sb);
        });
    }

    @Override
    public boolean equals(Object obj) {
        return map.equals(((KVReplicatedStateMachine) obj).map);
    }

    /**
     * Adds a key value pair to the state machine. The data is not added directly, but sent to the RAFT leader and only
     * added to the hashmap after the change has been committed (by majority decision). The actual change will be
     * applied with callback {@link #apply(byte[], int, int)}.
     *
     * @param key The key to be added.
     * @param val The value to be added
     * @return Null, or the previous value associated with key (if present)
     */
    public V put(K key, V val) throws Exception {
        return invoke(KVCommandType.PUT, key, val, false);
    }

    /**
     * Returns the value for a given key. Currently, the hashmap is accessed directly to return the value, possibly
     * returning stale data. In the next version, we'll look into returning a value based on consensus, or returning
     * the value from the leader (configurable).
     *
     * @param key The key
     * @return The value associated with key (might be stale)
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * Removes a key-value pair from the state machine. The data is not removed directly from the hashmap, but an
     * update is sent via RAFT and the actual removal from the hashmap is only done when that change has been committed.
     *
     * @param key The key to be removed
     */
    public V remove(K key) throws Exception {
        return invoke(KVCommandType.REMOVE, key, null, true);
    }

    /**
     * Returns the number of elements in the RSM
     */
    public int size() {
        return map.size();
    }

    ///////////////////////////////////////// StateMachine callbacks /////////////////////////////////////

    @Override
    public byte[] apply(byte[] data, int offset, int length) throws Exception {
        KeyValueWapper<K, V> kvKeyValueWapper = new KeyValueWapper<K, V>();
        Command<KVCommandType, KeyValueWapper<K, V>> command = new Command<KVCommandType, KeyValueWapper<K, V>>();
        SerializeTool.deserialize(data, command);
        KVCommandType type = command.getType();
        K key = command.getData().getKey();
        V val = command.getData().getValue();
        switch (type) {
            case PUT:
                V old_val = map.put(key, val);
                notifyPut(key, val, old_val);
                return old_val == null ? null : SerializeTool.serialize(old_val);
            case REMOVE:
                old_val = map.remove(key);
                notifyRemove(key, old_val);
                return old_val == null ? null : SerializeTool.serialize(old_val);
            default:
                throw new IllegalArgumentException("command " + command + " is unknown");
        }
    }

    @Override
    public void readContentFrom(DataInput in) throws Exception {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int keyValueLength = in.readInt();
            byte[] keyValueBuf = new byte[keyValueLength];
            in.readFully(keyValueBuf);
            KeyValueWapper<K, V> keyValueWapper = new KeyValueWapper<K, V>();
            SerializeTool.deserialize(keyValueBuf, keyValueWapper);
            K key = keyValueWapper.getKey();
            V val = keyValueWapper.getValue();
            map.put(key, val);
        }
    }

    @Override
    public void writeContentTo(DataOutput out) throws Exception {
        int size = map.size();
        out.writeInt(size); //TODO
        for (Map.Entry<K, V> entry : map.entrySet()) {
            KeyValueWapper<K, V> keyValueWapper = new KeyValueWapper<K, V>(entry.getKey(), entry.getValue());
            byte[] keyValueBuf = SerializeTool.serialize(keyValueWapper);
            out.writeInt(keyValueBuf.length);
            out.write(keyValueBuf);
        }
    }

    ///////////////////////////////////// End of StateMachine callbacks ///////////////////////////////////

    public String toString() {
        return map.toString();
    }

    protected V invoke(KVCommandType commandType, K key, V val, boolean ignore_return_value) throws Exception {
        KeyValueWapper<K, V> kvKeyValueWapper = new KeyValueWapper<K, V>(key, val);
        Command<KVCommandType, KeyValueWapper<K, V>> command = new Command<KVCommandType, KeyValueWapper<K, V>>(commandType, kvKeyValueWapper);
        byte[] cmdBytes;
        try {
            cmdBytes = SerializeTool.serialize(command);
        } catch (Exception ex) {
            throw new Exception("serialization failure (key=" + key + ", val=" + val + ")", ex);
        }

        byte[] rsp = raft.set(cmdBytes, 0, cmdBytes.length, repl_timeout, TimeUnit.MILLISECONDS);
        return ignore_return_value ? null : (val != null ? SerializeTool.deserialize(rsp, val) : null);
    }

    protected void notifyPut(K key, V val, V old_val) {
        for (Notification<K, V> n : listeners) {
            try {
                n.put(key, val, old_val);
            } catch (Throwable t) {
            }
        }
    }

    protected void notifyRemove(K key, V old_val) {
        for (Notification<K, V> n : listeners) {
            try {
                n.remove(key, old_val);
            } catch (Throwable t) {
            }
        }
    }

    public interface Notification<K, V> {

        void put(K key, V val, V old_val);

        void remove(K key, V old_val);

    }

}