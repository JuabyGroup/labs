/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2013 Oracle and/or its affiliates. All rights reserved.
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

package com.juaby.labs.rpc.client;

import com.juaby.labs.rpc.config.ServiceConfig;
import com.juaby.labs.rpc.config.ServiceConfigHelper;
import com.juaby.labs.rpc.message.RpcMessage;
import com.juaby.labs.rpc.proxy.ServiceClassInfo;
import com.juaby.labs.rpc.transport.RpcTransport;
import com.juaby.labs.rpc.transport.RpcTransportFactory;
import com.juaby.labs.rpc.util.SerializeTool;
import com.juaby.labs.rpc.message.RequestMessageBody;
import com.juaby.labs.rpc.message.ResponseMessageBody;
import com.juaby.labs.rpc.util.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Simple GIOP client
 * 
 * @author Alexey Stashok
 */
public class RpcClient {

    public static <R> R sendMessage(RequestMessageBody requestMessageBody) {
        RpcTransport transport = null;
        //TODO
        Endpoint endpoint = null;
        ServiceConfig serviceConfig = ServiceConfigHelper.getConfig(requestMessageBody.getService());
        if (serviceConfig != null) {
            endpoint = serviceConfig.getEndpoint();
        } else {
            //TODO laodblance
            endpoint = EndpointHelper.cache(requestMessageBody.getService()).iterator().next();
        }

        final RpcFutureImpl<ResponseMessageBody> resultFuture = RpcSafeFutureImpl.create();
        Integer messageId = MessageIdGenerator.id();
        ResponseMessageBody<R> rcvMessage = new ResponseMessageBody<R>();
        try {
            // Connect client to the GIOP server
            transport = RpcTransportFactory.getTransport(endpoint, serviceConfig);

            ServiceClassInfo.MethodInfo methodInfo = ServiceClassInfoHelper.get(requestMessageBody.getService()).getMethods().get(requestMessageBody.getMethod());
            String transportKey = null;
            if(methodInfo.isCallback()) {
                //TODO
                transportKey = transport.getHost() + ":" + transport.getPort() + ":" + requestMessageBody.getService() + ":" + requestMessageBody.getMethod();
                RpcCallbackHandler.addClientCallbackProxy(transportKey, (RpcCallback) requestMessageBody.getParams()[methodInfo.getCallbackIndex()]);
                requestMessageBody.getParams()[methodInfo.getCallbackIndex()] = null;
            }

            // Initialize sample GIOP message
            byte[] testMessage = SerializeTool.serialize(requestMessageBody);
            RpcMessage sentMessage = new RpcMessage((byte) 1, (byte) 2,
                    (byte) 0x0F, (byte) 0, testMessage);

            sentMessage.setId(messageId);
            sentMessage.setTotalLength(ServiceConfig.HEADER_SIZE + testMessage.length);

            ResultFutureHelper.map().put(messageId, resultFuture);

            transport.sendMessage(sentMessage);

            if (serviceConfig.isAsync()) {
                return null;
            } else {
                rcvMessage = ResultFutureHelper.result(messageId, serviceConfig.getTimeout());
            }
        } catch (InterruptedException e) {
            //TODO
        } catch (ExecutionException e) {
            //TODO
        } catch (TimeoutException e) {
            //TODO
        } catch (Exception e) {
            //TODO
        } finally {
            //TODO
            if (transport != null) {
                transport.release(endpoint);
            }
            ResultFutureHelper.map().remove(messageId);
        }
        return rcvMessage.getBody();
    }

}