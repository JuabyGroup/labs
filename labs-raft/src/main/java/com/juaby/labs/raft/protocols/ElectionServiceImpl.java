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
        electionProtocol.handleEvent(heartbeatRequest);
        int term = heartbeatRequest.term();
        Endpoint leader = heartbeatRequest.getLeader();
        electionProtocol.handleHeartbeat(term, leader);
    }

    @Override
    public void vote(VoteRequest voteRequest, RpcCallback<VoteResponse, Boolean> callback) {
        if (voteRequest != null) {
            electionProtocol.handleEvent(voteRequest);
            int term = voteRequest.term();
            int last_log_term = voteRequest.getLast_log_term();
            int last_log_index = voteRequest.getLast_log_index();
            Endpoint candidateId = voteRequest.getCandidateId();
            boolean send_vote_rsp = electionProtocol.handleVoteRequest(candidateId, term, last_log_term, last_log_index);
            if (send_vote_rsp) {
                electionProtocol.sendVoteResponse(candidateId, term, callback); // raft.current_term);
            }
        }
        return;
    }

}