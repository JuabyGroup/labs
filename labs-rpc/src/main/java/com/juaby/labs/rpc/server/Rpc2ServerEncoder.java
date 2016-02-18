/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.message.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;

import java.io.ObjectInputStream;

/**
 * An encoder which serializes a Java object into a {@link ByteBuf}.
 * <p>
 * Please note that the serialized form this encoder produces is not
 * compatible with the standard {@link ObjectInputStream}.  Please use
 * {@link ObjectDecoder} or {@link ObjectDecoderInputStream} to ensure the
 * interoperability with this encoder.
 */
public class Rpc2ServerEncoder extends MessageToByteEncoder<RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {
        // GIOP header
        out.writeBytes(msg.getGIOPHeader());

        // Major version
        out.writeByte(msg.getMajor());

        // Minor version
        out.writeByte(msg.getMinor());

        // Flags
        out.writeByte(msg.getFlags());

        // Value
        out.writeByte(msg.getValue());

        // ID
        out.writeInt(msg.getId());

        // Total length
        out.writeInt(msg.getTotalLength());

        // Body length
        out.writeInt(msg.getBodyLength());

        // Body
        out.writeBytes(msg.getBody());
    }

}
