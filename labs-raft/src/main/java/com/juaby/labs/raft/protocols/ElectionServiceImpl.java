package com.juaby.labs.raft.protocols;

import com.juaby.labs.rpc.util.Endpoint;
import com.juaby.labs.rpc.util.RpcCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs leader election. Starts an election timer on connect and starts an election when the timer goes off and
 * no heartbeats have been received. Runs a heartbeat task when leader.
 *
 * @author Bela Ban
 * @since 0.1
 */

/**
 * Protocol performing leader election according to the RAFT paper
 */
public class ElectionServiceImpl implements ElectionService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private ElectionProtocol electionProtocol;

    public ElectionServiceImpl(ElectionProtocol electionProtocol) {
        this.electionProtocol = electionProtocol;
    }

    @Override
    public void heartbeat(HeartbeatRequest heartbeatRequest) {
        // drop the message if hdr.term < raft.current_term, else accept
        // if hdr.term > raft.current_term -> change to follower
        int rc = electionProtocol.raft.currentTerm(heartbeatRequest.term);
        if (rc < 0)
            return;
        if (rc > 0) { // a new term was set
            electionProtocol.changeRole(Role.Follower);
            electionProtocol.voteFor(null); // so we can vote again in this term
        }

        int term = heartbeatRequest.term();
        Endpoint leader = heartbeatRequest.getLeader();
        electionProtocol.handleHeartbeat(term, leader);
    }

    @Override
    public VoteResponse vote(VoteRequest voteRequest, RpcCallback<VoteResponse, Boolean> callback) {

        if (voteRequest != null) {
            int term = voteRequest.term();
            int last_log_term = voteRequest.getLast_log_term();
            int last_log_index = voteRequest.getLast_log_index();
            Endpoint candidateId = voteRequest.getCandidateId();

            // drop the message if hdr.term < raft.current_term, else accept
            // if hdr.term > raft.current_term -> change to follower
            int rc = electionProtocol.raft.currentTerm(voteRequest.term);
            if (rc < 0)
                electionProtocol.sendVoteResponse(candidateId, term, false, callback); // raft.current_term);
            if (rc > 0) { // a new term was set
                electionProtocol.changeRole(Role.Follower);
                electionProtocol.voteFor(null); // so we can vote again in this term
            }

            boolean send_vote_rsp = electionProtocol.handleVoteRequest(candidateId, term, last_log_term, last_log_index);
            if (send_vote_rsp) {
                electionProtocol.sendVoteResponse(candidateId, term, true, callback); // raft.current_term);
            }
        }
        return null;
    }

}