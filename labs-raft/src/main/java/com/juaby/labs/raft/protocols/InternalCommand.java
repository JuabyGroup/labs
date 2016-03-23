package com.juaby.labs.raft.protocols;

/**
 * Internal command to be added to the log, e.g. adding or removing a server
 *
 * @author Bela Ban
 * @since 0.2
 */
public class InternalCommand {

    protected Type type;
    protected String name;

    public InternalCommand() { // marshalling
    }

    public InternalCommand(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type type() {
        return type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object execute(RAFT raft) throws Exception {
        switch (type) {
            case addServer:
                raft._addServer(name);
                break;
            case removeServer:
                raft._removeServer(name);
                break;
        }
        return null;
    }

    @Override
    public String toString() {
        return type + (type == Type.noop ? "" : "(" + name + ")");
    }

    public enum Type {
        addServer,
        removeServer,
        noop
    }

}
