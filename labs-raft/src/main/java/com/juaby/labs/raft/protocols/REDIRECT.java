package com.juaby.labs.raft.protocols;

/**
 * Protocol that redirects RAFT commands from clients to the actual RAFT leader. E.g. if a client issues a set(), but
 * the current mode is not the leader, the set() is redirected to the leader and the client blocked until the set()
 * has been committed by a majority of nodes.
 *
 * @author Bela Ban
 * @since 0.1
 */

/** Redirects requests to current leader */
public class REDIRECT /** implements Settable, DynamicMembership */ {

    /**

    // When moving to JGroups -> add to jg-protocol-ids.xml
    protected static final short REDIRECT_ID = 522;

    // When moving to JGroups -> add to jg-magic-map.xml
    protected static final short REDIRECT_HDR = 4000;

    public enum RequestType {
        SET_REQ,
        ADD_SERVER,
        REMOVE_SERVER,
        RSP
    }

    protected RaftProtocol raft;
    protected volatile Endpoint local_addr;

    protected final AtomicInteger request_ids = new AtomicInteger(1);

    // used to correlate redirect requests and responses: keys are request-ids and values futures
    protected final Map<Integer, CompletableFuture<byte[]>> requests = new HashMap<>();

    @Override
    public byte[] set(byte[] buf, int offset, int length) throws Exception {
        CompletableFuture<byte[]> future = setAsync(buf, offset, length);
        return future.get();
    }

    @Override
    public byte[] set(byte[] buf, int offset, int length, long timeout, TimeUnit unit) throws Exception {
        CompletableFuture<byte[]> future = setAsync(buf, offset, length);
        return future.get(timeout, unit);
    }

    @Override
    public CompletableFuture<byte[]> setAsync(byte[] buf, int offset, int length) {
        Endpoint leader = leader("set()");

        // we are the current leader: pass the call to the RAFT protocol
        if (Objects.equals(local_addr, leader))
            return raft.setAsync(buf, offset, length);

        // add a unique ID to the request table, so we can correlate the response to the request
        int req_id = request_ids.getAndIncrement();
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        synchronized (requests) {
            requests.put(req_id, future);
        }

        // we're not the current leader -> redirect request to leader and wait for response or timeout
        log.trace("%s: redirecting request %d to leader %s", local_addr, req_id, leader);
        Message redirect = new Message(leader, buf, offset, length)
                .putHeader(id, new RedirectHeader(RequestType.SET_REQ, req_id, false));
        down_prot.down(new Event(Event.MSG, redirect));
        return future;
    }

    @Override
    public CompletableFuture<byte[]> addServer(String name) throws Exception {
        return changeServer(name, true);
    }

    @Override
    public CompletableFuture<byte[]> removeServer(String name) throws Exception {
        return changeServer(name, false);
    }

    public void init() throws Exception {
        //super.init();
        raft = Cache.getRaftProtocol();

        local_addr = null; //TODO
    }

    protected void handleEvent(Message msg, RedirectHeader hdr) {
        Address sender = msg.src();
        switch (hdr.type) {
            case SET_REQ:
                log.trace("%s: received redirected request %d from %s", local_addr, hdr.corr_id, sender);
                ResponseHandler rsp_handler = new ResponseHandler(sender, hdr.corr_id);
                try {
                    raft.setAsync(msg.getRawBuffer(), msg.getOffset(), msg.getLength())
                            .whenComplete(rsp_handler);
                } catch (Throwable t) {
                    rsp_handler.apply(t);
                }
                break;
            case ADD_SERVER:
            case REMOVE_SERVER:
                rsp_handler = new ResponseHandler(sender, hdr.corr_id);
                InternalCommand.Type type = hdr.type == RequestType.ADD_SERVER ? InternalCommand.Type.addServer : InternalCommand.Type.removeServer;
                try {
                    raft.changeMembers(new String(msg.getRawBuffer(), msg.getOffset(), msg.getLength()), type)
                            .whenComplete(rsp_handler);
                } catch (Throwable t) {
                    rsp_handler.apply(t);
                }
                break;
            case RSP:
                CompletableFuture<byte[]> future = null;
                synchronized (requests) {
                    future = requests.remove(hdr.corr_id);
                }
                if (future != null) {
                    log.trace("%s: received response for redirected request %d from %s", local_addr, hdr.corr_id, sender);
                    if (!hdr.exception)
                        future.complete(msg.getBuffer());
                    else {
                        try {
                            Throwable t = (Throwable) Util.objectFromByteBuffer(msg.getBuffer());
                            future.completeExceptionally(t);
                        } catch (Exception e) {
                            log.error("failed deserializing exception", e);
                        }
                    }
                }
                break;
            default:
                log.error("type %d not known", hdr.type);
                break;
        }
    }

    protected Endpoint leader(String req_type) {
        Endpoint leader = raft.leader();
        if (leader == null) {
            throw new RuntimeException(String.format("there is currently no leader to forward %s request to", req_type));
        }
        if (view != null && !view.containsMember(leader))
            throw new RuntimeException("leader " + leader + " is not member of view " + view);
        return leader;
    }

    protected CompletableFuture<byte[]> changeServer(String name, boolean add) throws Exception {
        Endpoint leader = leader("addServer()/removeServer()");

        // we are the current leader: pass the call to the RAFT protocol
        if (Objects.equals(local_addr, leader)) {
            return raft.changeMembers(name, add ? InternalCommand.Type.addServer : InternalCommand.Type.removeServer);
        }

        // add a unique ID to the request table, so we can correlate the response to the request
        int req_id = request_ids.getAndIncrement();
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        synchronized (requests) {
            requests.put(req_id, future);
        }

        // we're not the current leader -> redirect request to leader and wait for response or timeout
        log.trace("%s: redirecting request %d to leader %s", local_addr, req_id, leader);
        byte[] buffer = Util.stringToBytes(name);
        Message redirect = new Message(leader, buffer)
                .putHeader(id, new RedirectHeader(add ? RequestType.ADD_SERVER : RequestType.REMOVE_SERVER, req_id, false));
        down_prot.down(new Event(Event.MSG, redirect));
        return future;
    }


    protected class ResponseHandler implements BiConsumer<byte[], Throwable> {

        protected final Endpoint dest;
        protected final int corr_id;

        public ResponseHandler(Endpoint dest, int corr_id) {
            this.dest = dest;
            this.corr_id = corr_id;
        }

        @Override
        public void accept(byte[] buf, Throwable ex) {
            if (ex != null)
                apply(ex);
            else
                apply(buf);
        }

        protected void apply(byte[] arg) {
            Message msg = new Message(dest, arg).putHeader(id, new RedirectHeader(RequestType.RSP, corr_id, false));
            down_prot.down(new Event(Event.MSG, msg));
        }

        protected void apply(Throwable t) {
            try {
                byte[] buf = Util.objectToByteBuffer(t);
                Message msg = new Message(dest, buf).putHeader(id, new RedirectHeader(RequestType.RSP, corr_id, true));
                down_prot.down(new Event(Event.MSG, msg));
            } catch (Exception ex) {
                log.error("failed serializing exception", ex);
            }
        }

    }

    public static class RedirectHeader {

        protected RequestType type;
        protected int corr_id;   // correlation ID at the sender, so responses can unblock requests (keyed by ID)
        protected boolean exception; // true if RSP is an exception

        public RedirectHeader() {
        }

        public RedirectHeader(RequestType type, int corr_id, boolean exception) {
            this.type = type;
            this.corr_id = corr_id;
            this.exception = exception;
        }

        public String toString() {
            return new StringBuilder(type.toString()).append(", corr_id=").append(corr_id)
                    .append(", exception=").append(exception).toString();
        }

    }

     */

}