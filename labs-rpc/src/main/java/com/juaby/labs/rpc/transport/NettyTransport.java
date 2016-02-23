package com.juaby.labs.rpc.transport;

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
    public void sendMessage(Object message) {
        channel.write(message, channel.voidPromise());
    }

}
