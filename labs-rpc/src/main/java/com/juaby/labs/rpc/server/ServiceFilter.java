/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.exception.RpcException;
import com.juaby.labs.rpc.message.RequestMessageBody;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ProxyHelper;
import com.juaby.labs.rpc.proxy.RpcCallbackProxyGenerator;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.test.CallbackTest;
import com.juaby.labs.rpc.transport.GrizzlyTransport;
import com.juaby.labs.rpc.util.RpcCallback;
import com.juaby.labs.rpc.util.RpcCallbackHandler;
import com.juaby.labs.rpc.util.SerializeTool;
import com.juaby.labs.rpc.util.ServiceClassInfoHelper;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChain;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;

/**
 * Implementation of {@link FilterChain} filter, which replies with the request
 * message.
 * 
 * @author Alexey Stashok
 */
public class ServiceFilter extends BaseFilter {

    /**
     * Handle just read operation, when some message has come and ready to be
     * processed.
     *
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws IOException
     */
    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        // Peer address is used for non-connected UDP Connection :)
        final Object peerAddress = ctx.getAddress();

        InetSocketAddress inetSocketAddress = (InetSocketAddress) peerAddress;

        final RpcMessage message = ctx.getMessage();

        RequestMessageBody requestMessageBody = new RequestMessageBody();
        SerializeTool.deserialize(message.getBody(), requestMessageBody);
        RpcServiceHandler rpcServiceHandler = ProxyHelper.getProxyInstance(requestMessageBody.getService() + requestMessageBody.getMethod());
        ServiceClassInfo serviceClassInfo = ServiceClassInfoHelper.get(requestMessageBody.getService());
        ServiceClassInfo.MethodInfo methodInfo = serviceClassInfo.getMethods().get(requestMessageBody.getMethod());
        String transportKey = null;
        if(methodInfo.isCallback()) {
            //TODO
            transportKey = inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort() + ":" + requestMessageBody.getService() + ":" + requestMessageBody.getMethod();
            RpcCallbackHandler.addCallbackRpcTransport(transportKey, new GrizzlyTransport(ctx.getConnection()));
            RpcCallback callback = RpcCallbackHandler.getServerCallbackProxy(transportKey);
            if (callback == null) {
                try {
                    callback = new RpcCallbackProxyGenerator().newInstance(serviceClassInfo, transportKey);
                } catch (IllegalAccessException e) {
                    //TODO
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    //TODO
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    //TODO
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    //TODO
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    //TODO
                    e.printStackTrace();
                }
                RpcCallbackHandler.addServerCallbackProxy(transportKey, callback);
            }
            requestMessageBody.getParams()[methodInfo.getCallbackIndex()] = callback;
        }

        ResponseMessageBody messageBody;
        try {
            messageBody = rpcServiceHandler.handler(requestMessageBody.getParams());
            if (messageBody == null) {
                messageBody = new ResponseMessageBody();
            }
        } catch (Exception e) {
            messageBody = new ResponseMessageBody(new RpcException(e));
        }

        messageBody.setService(requestMessageBody.getService());
        messageBody.setMethod(requestMessageBody.getMethod());

        byte[] body = SerializeTool.serialize(messageBody);
        message.setBodyLength(body.length);
        message.setBody(body);
        ctx.write(peerAddress, message, null);

        return ctx.getStopAction();
    }

}
