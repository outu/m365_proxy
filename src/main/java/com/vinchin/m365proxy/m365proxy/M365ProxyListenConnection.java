/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyListenConnection.java:Listen to m365_ client_* Connection
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.*;


public class M365ProxyListenConnection {

    public static final Logger logger = LoggerFactory.getLogger(M365ProxyListenConnection.class);
    /**
     * public values is shared by socket work thread in attachment
     */
    public int _listenPort;

    public String _processUuid;

    public boolean _stop = false;

    public volatile Map<String, Thread> _workThreadManager = new HashMap<>();

    private int _threadPoolNum;

    private ExecutorService _executorService;

    private AsynchronousServerSocketChannel _serverChannel;

    private AsynchronousChannelGroup _channelGroup;

    /**
     * @Description init socket port
     * @param port socket port
     * @param uuid process uuid
     * @param threadPoolNum Socket listens to the number of threads in the asynchronous execution thread pool
     */
    public void init(int port, String uuid, int threadPoolNum){
        _listenPort = port;
        _processUuid = uuid;
        _threadPoolNum = threadPoolNum;
    }

    /**
     * @Description start socket listening
     * @return true-success false-failed
     */
    public int run() {
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
            // Initialize fixed length thread pool
            _executorService = Executors.newFixedThreadPool(_threadPoolNum);
            _channelGroup = AsynchronousChannelGroup.withThreadPool(_executorService);
            // Init AsynchronousServerSocketChannel
            _serverChannel = AsynchronousServerSocketChannel.open(_channelGroup);
            _serverChannel.bind(new InetSocketAddress(_listenPort));
            logger.debug("server starting at port " +_listenPort + "..");
            // Listen to client connections, but in AIO, each accept can only receive one client,
            // so you need to call accept again in the processing logic to start the next listen, similar to chain call
            /**
             * @Description Start asynchronous thread to wait for socket connection
             * @param Asynchronous socket threads share data
             * @param Asynchronous Socket Channel handler
             */
            _serverChannel.accept(this, new M365ProxyWorkThread());
            while(true){
                if (_stop){
                    M365ProxyGlobalVals.g_service_exit_flag = true;
                    break;
                }
                TimeUnit.SECONDS.sleep(1L);
            }
            destroy();
        } catch (IOException | InterruptedException | RuntimeException e) {
            logger.error("start socket listening error: " + e.getMessage());
            destroy();
            ret = BD_NET_BIND_ERROR.getCode();
        }

        return ret;
    }

    public ExecutorService getService() {
        return _executorService;
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return _serverChannel;
    }

    /**
     * @Description close all thread and clear all thread cache
     */
    public void destroy(){
        try {
            if (!_workThreadManager.isEmpty()){
                for (String key : _workThreadManager.keySet()){
                    Thread workThread = _workThreadManager.get(key);
                    if (workThread.isAlive() && !workThread.isInterrupted()){
                        workThread.interrupt();
                    }
                }
                _workThreadManager.clear();
            }

            if (!_channelGroup.isShutdown()){
                _channelGroup.shutdown();
                _channelGroup= null;
            }

            if (!_executorService.isShutdown()){
                _executorService.shutdown();
                _executorService = null;
            }

            if (_serverChannel != null){
                _serverChannel.close();
            }
        } catch (Exception e){
            logger.warn("destroy error: " + e.getMessage());
        }
    }
}
