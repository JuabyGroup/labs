package com.juaby.labs.raft.demos;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.atomic.Counter;
import org.jgroups.protocols.raft.ELECTION;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.raft.blocks.CounterService;
import org.jgroups.util.Util;

/**
 * Demo of CounterService. When integrating with JGroups, this class should be removed and the original demo in JGroups
 * should be modified to accept different CounterService implementations.
 */
public class CounterServiceDemo {
    protected JChannel       ch;
    protected CounterService counter_service;

    void start(String props, String name, long repl_timeout, boolean allow_dirty_reads, boolean follower) throws Exception {
        ch=new JChannel(props).name(name);
        counter_service=new CounterService(ch).raftId(name).replTimeout(repl_timeout).allowDirtyReads(allow_dirty_reads);
        if(follower)
            disableElections(ch);
        ch.setReceiver(new ReceiverAdapter() {
            public void viewAccepted(View view) {
                System.out.println("-- view: " + view);
            }
        });

        try {
            ch.connect("cntrs");
            loop();
        }
        finally {
            Util.close(ch);
        }
    }



    protected void loop() throws Exception {
        Counter counter=counter_service.getOrCreateCounter("counter", 1);
        boolean looping=true;
        while(looping) {
            try {
                int key=Util.keyPress("[1] Increment [2] Decrement [3] Compare and set [4] Dump log\n" +
                                        "[8] Snapshot [9] Increment N times [x] Exit\n" +
                                        "first-applied=" + firstApplied() +
                                        ", last-applied=" + counter_service.lastApplied() +
                                        ", commit-index=" + counter_service.commitIndex() +
                                        ", log size=" + Util.printBytes(logSize()) + "\n");

                switch(key) {
                    case '1':
                        long val=counter.incrementAndGet();
                        System.out.printf("%s: %s\n", counter.getName(), val);
                        break;
                    case '2':
                        val=counter.decrementAndGet();
                        System.out.printf("%s: %s\n", counter.getName(), val);
                        break;
                    case '3':
                        long expect=Util.readLongFromStdin("expected value: ");
                        long update=Util.readLongFromStdin("update: ");
                        if(counter.compareAndSet(expect, update)) {
                            System.out.println("-- set counter \"" + counter.getName() + "\" to " + update + "\n");
                        }
                        else {
                            System.err.println("failed setting counter \"" + counter.getName() + "\" from " + expect +
                                                 " to " + update + ", current value is " + counter.get() + "\n");
                        }
                        break;
                    case '4':
                        dumpLog();
                        break;
                    case '8':
                        counter_service.snapshot();
                        break;
                    case '9':
                        int NUM=Util.readIntFromStdin("num: ");
                        System.out.println("");
                        int print=NUM / 10;
                        long retval=0;
                        long start=System.currentTimeMillis();
                        for(int i=0; i < NUM; i++) {
                            retval=counter.incrementAndGet();
                            if(i > 0 && i % print == 0)
                                System.out.println("-- count=" + retval);
                        }
                        long diff=System.currentTimeMillis() - start;
                        System.out.println("\n" + NUM + " incrs took " + diff + " ms; " + (NUM / (diff / 1000.0)) + " ops /sec\n");
                        break;
                    case 'x':
                        looping=false;
                        break;
                    case '\n':
                        System.out.println(counter.getName() + ": " + counter.get() + "\n");
                        break;
                }
            }
            catch(Throwable t) {
                System.err.println(t.toString());
            }
        }
    }

    protected void dumpLog() {
        System.out.println("\nindex (term): command\n---------------------");
        counter_service.dumpLog();
        System.out.println("");
    }

    protected int firstApplied() {
        RAFT raft=(RAFT)ch.getProtocolStack().findProtocol(RAFT.class);
        return raft.log().firstAppended();
    }

    protected int logSize() {
        return counter_service.logSize();
    }

    protected static void disableElections(JChannel ch) {
        ELECTION election=(ELECTION)ch.getProtocolStack().findProtocol(ELECTION.class);
        if(election != null)
            election.noElections(true);
    }



    public static void main(final String[] args) throws Exception {
        String properties="raft.xml";
        String name=null;
        long repl_timeout=5000;
        boolean allow_dirty_reads=true;
        boolean follower=false;
        for(int i=0; i < args.length; i++) {
            if(args[i].equals("-props")) {
                properties=args[++i];
                continue;
            }
            if(args[i].equals("-name")) {
                name=args[++i];
                continue;
            }
            if(args[i].equals("repl_timeout")) {
                repl_timeout=Long.parseLong(args[++i]);
                continue;
            }
            if(args[i].equals("-allow_dirty_reads")) {
                allow_dirty_reads=Boolean.parseBoolean(args[++i]);
                continue;
            }
            if(args[i].equals("-follower")) {
                follower=true;
                continue;
            }
            help();
            return;
        }


        new CounterServiceDemo().start(properties, name, repl_timeout, allow_dirty_reads, follower);

    }

    private static void help() {
        System.out.println("CounterServiceDemo [-props props] [-name name] " +
                             "[-repl_timeout timeout] [-follower] [-allow_dirty_reads true|false]");
    }


}
