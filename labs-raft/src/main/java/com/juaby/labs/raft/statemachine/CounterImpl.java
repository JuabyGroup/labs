package com.juaby.labs.raft.statemachine;

/**
 * @author Bela Ban
 * @since 0.2
 */
public class CounterImpl implements Counter {

    protected final String name;
    protected final CounterServiceStateMachine counter_service; // to delegate all commands to

    public CounterImpl(String name, CounterServiceStateMachine counter_service) {
        this.name = name;
        this.counter_service = counter_service;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long get() {
        try {
            return counter_service.allowDirtyReads() ? counter_service._get(name) : counter_service.get(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void set(long new_value) {
        try {
            counter_service.set(name, new_value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean compareAndSet(long expect, long update) {
        try {
            return counter_service.compareAndSet(name, expect, update);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public long incrementAndGet() {
        try {
            return counter_service.incrementAndGet(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public long decrementAndGet() {
        try {
            return counter_service.decrementAndGet(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public long addAndGet(long delta) {
        try {
            return counter_service.addAndGet(name, delta);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(counter_service._get(name));
    }

}