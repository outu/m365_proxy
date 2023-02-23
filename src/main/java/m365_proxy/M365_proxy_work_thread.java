/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_work_thread.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import common.TypeConversion;
import common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class M365_proxy_work_thread implements CompletionHandler<AsynchronousSocketChannel, M365_proxy_listen_connection> {
    public static final Logger logger = LoggerFactory.getLogger(M365_proxy_work_thread.class);
    private AsynchronousSocketChannel _clientChannel;
    private M365_proxy_rpc_server _rpcServer;

    private String _threadUuid;

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, M365_proxy_listen_connection attachment) {
        try {
            logger.debug("accept one connection: " + socketChannel.getRemoteAddress());
            //start the next listen
            attachment.getServerChannel().accept(attachment, new M365_proxy_work_thread());
            addMapThreadUuidToThreadObj(attachment);
            logger.debug("add thread: " + _threadUuid + "to map thread obj");

            _clientChannel = socketChannel;
            _rpcServer = new M365_proxy_rpc_server();
            _rpcServer.init(socketChannel, attachment);
            _rpcServer.waitAndHandleRequest();

            destroy(attachment);
            logger.debug("connection closed, thread");
        } catch (IOException e) {
            logger.error("handle connection message failed: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.warn("handle connection message interrupted: " + e.getMessage());;
        }
    }

    @Override
    public void failed(Throwable exc, M365_proxy_listen_connection attachment) {
        logger.error("connection failed: " + exc.getMessage());
        //exc.printStackTrace();
        if (_rpcServer != null){
            _rpcServer = null;
        }
        if (_clientChannel != null && _clientChannel.isOpen() && attachment != null){
            attachment.getServerChannel().accept(attachment, this);
        }
    }

    private synchronized void addMapThreadUuidToThreadObj(M365_proxy_listen_connection attachment){
        _threadUuid = getThreadUuid();
        attachment._workThreadManager.put(_threadUuid, Thread.currentThread());
    }

    private synchronized void destroy(M365_proxy_listen_connection attachment) throws IOException {
        attachment._workThreadManager.remove(_threadUuid);
        _clientChannel.close();
        _rpcServer = null;
        _clientChannel = null;

    }

    private String getThreadUuid(){
        return Util.genericUuid();
    }
}
