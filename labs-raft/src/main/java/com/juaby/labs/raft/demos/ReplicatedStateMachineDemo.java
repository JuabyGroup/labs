package com.juaby.labs.raft.demos;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.jmx.JmxConfigurator;
import org.jgroups.protocols.raft.ELECTION;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.protocols.raft.Role;
import org.jgroups.raft.blocks.ReplicatedStateMachine;
import org.jgroups.util.Util;

/**
 * Demos {@link ReplicatedStateMachine}
 * @author Bela Ban
 * @since  0.1
 */
public class ReplicatedStateMachineDemo extends ReceiverAdapter implements RAFT.RoleChange {
    protected JChannel                              ch;
    protected ReplicatedStateMachine<String,Object> rsm;

    protected void start(String props, String name, boolean follower, long timeout) throws Exception {
        ch=new JChannel(props).name(name);
        rsm=new ReplicatedStateMachine<>(ch).raftId(name).timeout(timeout);
        if(follower)
            disableElections(ch);
        ch.setReceiver(this);

        try {
            ch.connect("rsm");
            Util.registerChannel(rsm.channel(), "rsm");
            rsm.addRoleChangeListener(this);
            rsm.addNotificationListener(new ReplicatedStateMachine.Notification<String,Object>() {
                @Override
                public void put(String key, Object val, Object old_val) {
                    System.out.printf("-- put(%s, %s) -> %s\n", key, val, old_val);
                }

                @Override
                public void remove(String key, Object old_val) {
                    System.out.printf("-- remove(%s) -> %s\n", key, old_val);
                }
            });
            loop();
            JmxConfigurator.unregisterChannel(rsm.channel(), Util.getMBeanServer(), "rsm");
        }
        finally {
            Util.close(ch);
        }
    }

    protected static void disableElections(JChannel ch) {
        ELECTION election=(ELECTION)ch.getProtocolStack().findProtocol(ELECTION.class);
        if(election != null)
            election.noElections(true);
    }

    protected void loop() {
        boolean looping=true;
        while(looping) {
            int input=Util.keyPress("[1] add [2] get [3] remove [4] show all [5] dump log [6] snapshot [x] exit\n" +
                                      "first-applied=" + firstApplied() +
                                      ", last-applied=" + rsm.lastApplied() +
                                      ", commit-index=" + rsm.commitIndex() +
                                      ", log size=" + Util.printBytes(logSize()) + "\n");
            switch(input) {
                case '1':
                    put(read("key"), read("value"));
                    break;
                case '2':
                    get(read("key"));
                    break;
                case '3':
                    remove(read("key"));
                    break;
                case '4':
                    System.out.println(rsm + "\n");
                    break;
                case '5':
                    dumpLog();
                    break;
                case '6':
                    try {
                        rsm.snapshot();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 'x':
                    looping=false;
                    break;
            }
        }
    }

    protected void put(String key, String value) {
        if(key == null || value == null) {
            System.err.printf("Key (%s) or value (%s) is null\n",key,value);
            return;
        }
        try {
            rsm.put(key, value);
        }
        catch(Throwable t) {
            System.err.println("failed setting " + key + "=" + value + ": " + t);
        }
    }

    protected void get(String key) {
        Object val=rsm.get(key);
        System.out.printf("-- get(%s) -> %s\n", key, val);
    }

    protected void remove(String key) {
        try {
            rsm.remove(key);
        }
        catch(Exception ex) {
            System.err.println("failed removing " + key + ": " + ex);
        }
    }

    protected static String read(String name) {
        try {
            return Util.readStringFromStdin(name + ": ");
        }
        catch(Exception e) {
            return null;
        }
    }

    protected int firstApplied() {
        RAFT raft=(RAFT)rsm.channel().getProtocolStack().findProtocol(RAFT.class);
        return raft.log().firstAppended();
    }

    protected int logSize() {
        return rsm.logSize();
    }

    protected void dumpLog() {
        System.out.println("\nindex (term): command\n---------------------");
        rsm.dumpLog();
        System.out.println("");
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("-- view change: " + view);
    }

    @Override
    public void roleChanged(Role role) {
        System.out.println("-- changed role to " + role);
    }

    public static void main(String[] args) throws Exception {
        String props="raft.xml";
        String name=null;
        boolean follower=false;
        long timeout=3000;

        for(int i=0; i < args.length; i++) {
            if(args[i].equals("-props")) {
                props=args[++i];
                continue;
            }
            if(args[i].equals("-name")) {
                name=args[++i];
                continue;
            }
            if(args[i].equals("-follower")) {
                follower=true;
                continue;
            }
            if(args[i].equals("-timeout")) {
                timeout=Long.parseLong(args[++i]);
                continue;
            }
            System.out.println("ReplicatedStateMachine [-props <config>] [-name <name>] [-follower] [-timeout timeout]");
            return;
        }
        new ReplicatedStateMachineDemo().start(props, name, follower, timeout);
    }


}
