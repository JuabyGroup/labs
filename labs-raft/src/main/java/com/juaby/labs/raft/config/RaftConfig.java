package com.juaby.labs.raft.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-3-28.
 */
public class RaftConfig {

    private String id;

    private int electionMinInterval = 100;

    private int electionMaxInterval = 500;

    private int heartbeatInterval = 30;

    private boolean dynamicMemberChanges = true;

    private String logClass = "org.jgroups.protocols.raft.LevelDBLog";;

    private String logName;

    private String logArgs;

    private String snapshotName;

    private int maxLogSize = 1_000_000;

    private int resendIinterval = 1000;

    private List<Member> members = new ArrayList<Member>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getElectionMinInterval() {
        return electionMinInterval;
    }

    public void setElectionMinInterval(int electionMinInterval) {
        this.electionMinInterval = electionMinInterval;
    }

    public int getElectionMaxInterval() {
        return electionMaxInterval;
    }

    public void setElectionMaxInterval(int electionMaxInterval) {
        this.electionMaxInterval = electionMaxInterval;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public boolean isDynamicMemberChanges() {
        return dynamicMemberChanges;
    }

    public void setDynamicMemberChanges(boolean dynamicMemberChanges) {
        this.dynamicMemberChanges = dynamicMemberChanges;
    }

    public String getLogClass() {
        return logClass;
    }

    public void setLogClass(String logClass) {
        this.logClass = logClass;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getLogArgs() {
        return logArgs;
    }

    public void setLogArgs(String logArgs) {
        this.logArgs = logArgs;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    public int getMaxLogSize() {
        return maxLogSize;
    }

    public void setMaxLogSize(int maxLogSize) {
        this.maxLogSize = maxLogSize;
    }

    public int getResendIinterval() {
        return resendIinterval;
    }

    public void setResendIinterval(int resendIinterval) {
        this.resendIinterval = resendIinterval;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public class Member {

        private String id;

        private String host;

        private int port;

        public Member() {
        }

        public Member(String id, String host, int port) {
            this.id = id;
            this.host = host;
            this.port = port;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }

}