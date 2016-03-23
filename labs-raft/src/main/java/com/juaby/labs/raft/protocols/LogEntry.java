package com.juaby.labs.raft.protocols;

/**
 * An element in a log. Captures the term and command to be applied to the state machine
 *
 * @author Bela Ban
 * @since 0.1
 */
public class LogEntry {

    protected int term;     // the term of this entry

    protected byte[] command;  // the command (interpreted by the state machine)

    protected int offset;   // may get removed (always 0)

    protected int length;   // may get removed (always command.length)

    protected boolean internal; // if true, the contents of the buffer are an InternalCommand

    public LogEntry() {
    }

    public LogEntry(int term, byte[] command) {
        this(term, command, 0, command != null ? command.length : 0);
    }

    public LogEntry(int term, byte[] command, int offset, int length) {
        this(term, command, offset, length, false);
    }

    public LogEntry(int term, byte[] command, int offset, int length, boolean internal) {
        this.term = term;
        this.command = command;
        this.offset = offset;
        this.length = length;
        this.internal = internal;
    }

    public int term() {
        return term;
    }

    public byte[] command() {
        return command;
    }

    public int offset() {
        return offset;
    }

    public int length() {
        return length;
    }

    public boolean internal() {
        return internal;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public byte[] getCommand() {
        return command;
    }

    public void setCommand(byte[] command) {
        this.command = command;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("term=").append(term).append(" (").append(command != null ? command.length : 0).append(" bytes)");
        if (internal) str.append(" [internal]");
        return str.toString();
    }

}
