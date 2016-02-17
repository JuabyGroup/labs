package com.juaby.labs.rpc.util;

/**
 * Interface, which will be used by Grizzly to notify about asynchronous I/O
 * operations status updates.
 *
 * @param <E> the type of the result
 *
 * @author Alexey Stashok
 */
public interface RpcCompletionHandler<E> {
    /**
     * The operation was cancelled.
     */
    void cancelled();

    /**
     * The operation was failed.
     * @param throwable error, which occurred during operation execution
     */
    void failed(Throwable throwable);

    /**
     * The operation was completed.
     * @param result the operation result
     *
     * Please note, for performance reasons the result object might be recycled
     * after returning from the completed method. So it's not guaranteed that
     * using of the result object is safe outside this method's scope.
     */
    void completed(E result);

    /**
     * The callback method may be called, when there is some progress in
     * operation execution, but it is still not completed
     * @param result the current result
     *
     * Please note, for performance reasons the result object might be recycled
     * after returning from the updated method. So it's not guaranteed that
     * using of the result object is safe outside this method's scope.
     */
    void updated(E result);

}

