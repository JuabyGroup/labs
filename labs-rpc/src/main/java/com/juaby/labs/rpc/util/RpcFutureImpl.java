package com.juaby.labs.rpc.util;

import java.util.concurrent.Future;

/**
 * {@link Future} interface, which has full control over the state.
 *
 * @see Future
 *
 * @author Alexey Stashok
 */
public interface RpcFutureImpl<R> extends RpcFuture<R> {
    /**
     * Get current result value without any blocking.
     *
     * @return current result value without any blocking.
     */
    R getResult();

    /**
     * Set the result value and notify about operation completion.
     *
     * @param result the result value
     */
    void result(R result);

    /**
     * Notify about the failure, occurred during asynchronous operation execution.
     *
     * @param failure
     */
    void failure(Throwable failure);

}
