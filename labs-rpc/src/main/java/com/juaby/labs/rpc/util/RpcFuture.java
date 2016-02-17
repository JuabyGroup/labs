package com.juaby.labs.rpc.util;

import java.util.concurrent.Future;

/**
 * Grizzly {@link Future} implementation.
 * Users can register additional {@link RpcCompletionHandler}s using
 * {@link #addCompletionHandler(com.juaby.labs.rpc.util.RpcCompletionHandler)}
 * to be notified once the asynchronous computation, represented by
 * this <tt>Future</tt>, is complete.
 *
 * A <tt>GrizzlyFuture</tt> instance can be recycled and reused.
 *
 * @param <R> the result type
 *
 * @author Alexey Stashok
 */
public interface RpcFuture<R> extends Future<R>, RpcCacheable {
    /**
     * Adds a {@link RpcCompletionHandler}, which will be notified once the
     * asynchronous computation, represented by this <tt>Future</tt>,
     * is complete.
     *
     * @param completionHandler {@link RpcCompletionHandler}
     * @since 2.3.4
     */
    void addCompletionHandler(RpcCompletionHandler<R> completionHandler);

    /**
     * Mark <tt>GrizzlyFuture</tt> as recyclable, so once result will come -
     * <tt>GrizzlyFuture</tt> object will be recycled and returned to a
     * thread local object pool.
     * You can consider to use this method, if you're not interested in using
     * this <tt>GrizzlyFuture</tt> object.
     *
     * @param recycleResult if <tt>true</tt> - the <tt>GrizzlyFuture</tt> result,
     * if it support recyclable mechanism, will be also recycled together
     * with this <tt>GrizzlyFuture</tt> object.
     *
     * @deprecated
     */
    void markForRecycle(boolean recycleResult);

    /**
     * Recycle <tt>GrizzlyFuture</tt> now.
     * This method could be used, if you're not interested in using this
     * <tt>GrizzlyFuture</tt> object, and you're sure this object is not used
     * by any other application part.
     *
     * @param recycleResult if <tt>true</tt> - the <tt>GrizzlyFuture</tt> result,
     * if it support recyclable mechanism, will be also recycled together
     * with this <tt>GrizzlyFuture</tt> object.
     */
    void recycle(boolean recycleResult);

}

