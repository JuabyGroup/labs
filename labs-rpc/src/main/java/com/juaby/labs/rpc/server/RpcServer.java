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

package com.juaby.labs.rpc.server;

import com.juaby.labs.rpc.config.ServiceConfig;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;

import java.io.IOException;

/**
 * Simple GIOP echo server
 * 
 * @author Alexey Stashok
 */
public class RpcServer implements Server {

    private String host = "localhost";
    private int port = 9098;

    private ServiceConfig config;

    private final TCPNIOTransport transport;

    public RpcServer() {
        this.transport = TCPNIOTransportBuilder.newInstance().build();
    }

    public RpcServer(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    public RpcServer(ServiceConfig config) {
        this();
        this.config = config;
    }

    @Override
    public void init() {
        //TODO
    }
    
    public void start() {
        LifeCycleFilter lifeCycleFilter = new LifeCycleFilter();
        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add filters to the chain
        filterChainBuilder.add(new TransportFilter());
        // Add lifecycle filter to track the connections
        filterChainBuilder.add(lifeCycleFilter);
        filterChainBuilder.add(new RpcServerFilter());
        filterChainBuilder.add(new ServiceFilter());

        transport.setProcessor(filterChainBuilder.build());

        try {
            // Bind server socket and start transport
            transport.bind(host, port);
            transport.start();
            /**
            System.out.println("Press 'q and ENTER' to exit, or just ENTER to see statistics...");

            do {
                printStats(lifeCycleFilter);
            } while (System.in.read() != 'q');
             */
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * Print the lifecycle statistics
     *
     * @param lifeCycleFilter the {@link LifeCycleFilter}
     */
    private void printStats(LifeCycleFilter lifeCycleFilter) {
        System.out.println("The total number of connections ever connected: " +
                lifeCycleFilter.getTotalConnections());
        System.out.println("The number of active connections: " +
                lifeCycleFilter.getActiveConnections().size());
    }

    @Override
    public void startup() {
        init();
        start();
    }

    @Override
    public void shutdown() {
        try {
            transport.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
