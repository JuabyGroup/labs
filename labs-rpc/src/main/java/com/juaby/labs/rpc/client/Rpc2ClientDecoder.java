package com.juaby.labs.rpc.client;

import com.juaby.labs.rpc.base.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * A decoder which deserializes the received {@link ByteBuf}s into Java
 * objects.
 * <p>
 * Please note that the serialized form this decoder expects is not
 * compatible with the standard {@link ObjectOutputStream}.  Please use
 * {@link ObjectEncoder} or {@link ObjectEncoderOutputStream} to ensure the
 * interoperability with this decoder.
 */
public class Rpc2ClientDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * Creates a new decoder with the specified maximum object size.
     *
     * @param maxObjectSize  the maximum byte length of the serialized object.
     *                       if the length of the received object is greater
     *                       than this value, {@link StreamCorruptedException}
     *                       will be raised.
     */
    public Rpc2ClientDecoder(int maxObjectSize) {
        super(maxObjectSize, 12, 4, -16, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        // Construct a GIOP message
        final RpcMessage giopMessage = new RpcMessage();

        // Set GIOP header bytes
        giopMessage.setGIOPHeader(frame.readByte(), frame.readByte(),
                frame.readByte(), frame.readByte());

        // Set major version
        giopMessage.setMajor(frame.readByte());

        // Set minor version
        giopMessage.setMinor(frame.readByte());

        // Set flags
        giopMessage.setFlags(frame.readByte());

        // Set value
        giopMessage.setValue(frame.readByte());

        // Read id
        // Set id
        giopMessage.setId(frame.readInt());

        // Set Total length
        final int totalLength = frame.readInt();
        giopMessage.setTotalLength(totalLength);

        // Set body length
        final int bodyLength = frame.readInt();
        giopMessage.setBodyLength(bodyLength);

        // Read body
        final byte[] body = new byte[bodyLength];
        frame.readBytes(body);
        // Set body
        giopMessage.setBody(body);

        return giopMessage;
    }

    @Override
    protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
        return buffer.slice(index, length);
    }

}
