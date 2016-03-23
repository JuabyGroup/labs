package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;

import java.util.Map;
import java.util.function.ObjIntConsumer;

/**
 * The interface for a persistent log. See doc/design/Log.txt for details.
 *
 * @author Bela Ban
 * @since 0.1
 */
public interface Log {

    /**
     * Called after the instance has been created
     *
     * @param log_name The name of the log. Implementations can create a DB or file named after this, e.g.
     *                 /tmp/<log_name></log_name>.log
     * @param args     A hashmap of configuration information (impl-dependent) to configure itself. May be null
     */
    void init(String log_name, Map<String, String> args) throws Exception;

    /**
     * Called when the instance is closed. Should release resource, ie. close a DB connection
     */
    void close();

    /**
     * Remove the persistent store, e.g. DB table, or file
     */
    void delete();

    /**
     * Returns the current term
     */
    int currentTerm();

    /**
     * Sets the current term
     */
    Log currentTerm(int new_term);

    /**
     * Returns the address of the candidate that this node voted for in the current term
     */
    Endpoint votedFor();

    /**
     * Sets the address of the member this node voted for in the current term. Only invoked once per term
     */
    Log votedFor(Endpoint member);

    /**
     * Returns the current commit index. (May get removed as the RAFT paper has this as in-memory attribute)
     */
    int commitIndex();

    /**
     * Sets commitIndex to a new value
     *
     * @param new_index The new index to set commitIndex to. May throw an exception if new_index > lastApplied()
     * @return the log
     */
    Log commitIndex(int new_index);

    /**
     * Returns the index of the first log entry
     */
    int firstAppended();

    /**
     * Returns the index of the last append operation (May get removed as this should be in-memory)<p/>
     * This value is set by {@link #append(int, boolean, LogEntry...)}
     */
    int lastAppended();

    /**
     * Append the entries starting at index. Advance last_appended by the number of entries appended.<p/>
     * This is used by the leader when appending entries before sending AppendEntries requests to cluster members.
     * Contrary to {@link #append(int, boolean, LogEntry...)}, no consistency check needs to be performed.
     *
     * @param index     The index at which to append the entries. Should be the same as lastAppended. LastAppended needs
     *                  to be incremented by the number of entries appended
     * @param overwrite If there is an existing entry and overwrite is true, overwrite it. Else throw an exception
     * @param entries   The entries to append
     */
    void append(int index, boolean overwrite, LogEntry... entries);


    /**
     * Delete all entries starting from start_index.
     * Updates current_term and last_appended accordingly
     *
     * @param index The index
     * @return The LogEntry, or null if none's present at index.
     */
    LogEntry get(int index);

    /**
     * Truncates the log up to (and excluding) index. All entries < index are removed. First = index.
     *
     * @param index If greater than commit_index, commit_index will be used instead
     */
    void truncate(int index);


    /**
     * Delete all entries starting from start_index (including the entry at start_index).
     * Updates current_term and last_appended accordingly
     *
     * @param start_index
     */
    void deleteAllEntriesStartingFrom(int start_index);


    /**
     * Applies function to all elements of the log in range [max(start_index,first_appended) .. min(last_appended,end_index)].
     *
     * @param function    The function to be applied
     * @param start_index The start index. If smaller than first_appended, first_appended will be used
     * @param end_index   The end index. If greater than last_appended, last_appended will be used
     */
    void forEach(ObjIntConsumer<LogEntry> function, int start_index, int end_index);

    /**
     * Applies a function to all elements in range [first_appended .. last_appended]
     */
    void forEach(ObjIntConsumer<LogEntry> function);

}
