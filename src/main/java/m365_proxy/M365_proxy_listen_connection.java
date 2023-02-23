/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_listen_connection.java:Listen to m365_ client_* Connection
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;


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

public class M365_proxy_listen_connection {

    public static final Logger logger = LoggerFactory.getLogger(M365_proxy_listen_connection.class);
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
    public Boolean run() throws RuntimeException, IOException {
        boolean ret = true;
        // Initialize fixed length thread pool
        _executorService = Executors.newFixedThreadPool(_threadPoolNum);
        _channelGroup = AsynchronousChannelGroup.withThreadPool(_executorService);

        try {
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
            _serverChannel.accept(this, new M365_proxy_work_thread());
            while(true){
                if (_stop){
                    M365_proxy_global_vals.g_service_exit_flag = true;
                    _serverChannel.close();
                    logger.debug("set global exit flag, close socket channel.");
                    break;
                }
                TimeUnit.SECONDS.sleep(1L);
            }
            destroy();
        } catch (IOException | InterruptedException e) {
            logger.error("start socket listening error: " + e.getMessage());
            ret = false;
        } finally {
            destroy();
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
     * close server channel
     * @throws IOException
     */
    public void destroy() throws IOException {

        if (_serverChannel != null){
            _serverChannel.close();
        }

        if (_workThreadManager != null){
            for (String key : _workThreadManager.keySet()){
                Thread workThread = _workThreadManager.get(key);
                if (workThread.isAlive()){
                    workThread.interrupt();
                }
            }
        }
        _channelGroup.shutdown();
        _executorService.shutdown();
        _workThreadManager.clear();
    }
}
