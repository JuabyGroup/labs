package com.juaby.labs.rpc.transport;

import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.util.Endpoint;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by juaby on 16-2-23.
 */
public class NettyTransport implements RpcTransport {

    private Channel channel;

    private String host;

    private int port;

    public NettyTransport(Channel channel) {
        this.channel = channel;
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channel.localAddress();
        this.host = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
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

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

}
