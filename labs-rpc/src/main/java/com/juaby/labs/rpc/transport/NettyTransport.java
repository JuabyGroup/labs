package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.Endpoint;
import io.netty.channel.Channel;

/**
 * Created by juaby on 16-2-23.
 */
public class NettyTransport implements RpcTransport {

    private Channel channel;

    public NettyTransport(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void sendMessage(RpcMessage message) {
        channel.writeAndFlush(message, channel.voidPromise());
    }

    @Override
    public void release(Endpoint endpoint) {
        //TODO
    }

    @Override
    public boolean isWritable() {
        return channel.isWritable();
    }

}
