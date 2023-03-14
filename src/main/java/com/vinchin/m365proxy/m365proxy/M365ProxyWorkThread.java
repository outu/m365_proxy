/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyWorkThread.java:
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
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.vinchin.m365proxy.common.Util;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_ERROR;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_SUCCESS;



public class M365ProxyWorkThread implements CompletionHandler<AsynchronousSocketChannel, M365ProxyListenConnection> {
    public static final Logger logger = LoggerFactory.getLogger(M365ProxyWorkThread.class);
    private AsynchronousSocketChannel clientChannel;

    private String _threadUuid;

    /**
     * @Description Invoked when an operation has completed.
     * @param socketChannel
     *          The result of the I/O operation.
     * @param attachment
     *          The object attached to the I/O operation when it was initiated.
     */
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, M365ProxyListenConnection attachment) {
        int ret = BD_GENERIC_SUCCESS.getCode();
        try {
            //start the next listen
            attachment.getServerChannel().accept(attachment, new M365ProxyWorkThread());

            logger.debug("accept one connection: " + socketChannel.getRemoteAddress());

            ret = addMapThreadUuidToThreadObj(attachment);

            if (BD_GENERIC_SUCCESS.getCode() == ret){
                logger.debug("add thread: " + _threadUuid + " to map thread obj");
                clientChannel = socketChannel;
                M365ProxyRpcServer rpcServer = new M365ProxyRpcServer();
                rpcServer.init(socketChannel, attachment);
                rpcServer.waitAndHandleRequest();
            }

            destroy(attachment);
            logger.debug("connection closed, thread: " + _threadUuid + " exit");
        } catch (Exception e) {
            destroy(attachment);
            logger.error("handle connection message failed: " + e.getMessage());
            logger.debug("connection closed, thread: " + _threadUuid + " exit");
        }
    }

    /**
     * @Description Invoked when an operation fails.
     * @param exc
     *          The exception to indicate why the I/O operation failed
     * @param attachment
     *          The object attached to the I/O operation when it was initiated.
     */
    @Override
    public void failed(Throwable exc, M365ProxyListenConnection attachment) {
        logger.warn("the I/O operation failed and build connection failed: " + exc.getMessage());
        attachment.getServerChannel().accept(attachment, new M365ProxyWorkThread());

        destroy(attachment);
    }

    /**
     * @Description Store the thread object in the shared map according to the thread uuid
     * @param attachment M365_proxy_listen_connection shared class
     */
    private synchronized int addMapThreadUuidToThreadObj(M365ProxyListenConnection attachment){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
            _threadUuid = getThreadUuid();
            attachment._workThreadManager.put(_threadUuid, Thread.currentThread());
        } catch (Exception e){
            logger.error("add thread: " + _threadUuid + "to work thread manager error: " + e.getMessage());
            ret = BD_GENERIC_ERROR.getCode();
        }

        return ret;
    }

    /**
     * @Description Delete all caches of thread, like share map and socket channel
     * @param attachment M365_proxy_listen_connection shared class
     */
    private synchronized void destroy(M365ProxyListenConnection attachment){
        try {
            boolean containKey = attachment._workThreadManager.containsKey(_threadUuid);
            if (containKey){
                attachment._workThreadManager.remove(_threadUuid);
            }

            clientChannel.close();
            clientChannel = null;
        } catch (IOException e){
            logger.warn("thread " + _threadUuid + "self destroy error: " + e.getMessage());
        }
    }

    private String getThreadUuid(){
        return Util.genericUuid();
    }
}
