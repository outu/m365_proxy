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

import common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


public class M365_proxy_work_thread implements CompletionHandler<AsynchronousSocketChannel, M365_proxy_listen_connection> {
    public static final Logger logger = LoggerFactory.getLogger(M365_proxy_work_thread.class);
    private AsynchronousSocketChannel _clientChannel;

    private String _threadUuid;

    /**
     * @Description Invoked when an operation has completed.
     * @param socketChannel
     *          The result of the I/O operation.
     * @param attachment
     *          The object attached to the I/O operation when it was initiated.
     */
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, M365_proxy_listen_connection attachment) {
        try {
            //start the next listen
            attachment.getServerChannel().accept(attachment, new M365_proxy_work_thread());

            logger.debug("accept one connection: " + socketChannel.getRemoteAddress());

            addMapThreadUuidToThreadObj(attachment);
            logger.debug("add thread: " + _threadUuid + " to map thread obj");

            _clientChannel = socketChannel;
            M365_proxy_rpc_server rpcServer = new M365_proxy_rpc_server();
            rpcServer.init(socketChannel, attachment);
            rpcServer.waitAndHandleRequest();

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
    public void failed(Throwable exc, M365_proxy_listen_connection attachment) {
        logger.warn("the I/O operation failed and build connection failed: " + exc.getMessage());
        attachment.getServerChannel().accept(attachment, new M365_proxy_work_thread());

        destroy(attachment);
    }

    /**
     * @Description Store the thread object in the shared map according to the thread uuid
     * @param attachment M365_proxy_listen_connection shared class
     */
    private synchronized void addMapThreadUuidToThreadObj(M365_proxy_listen_connection attachment){
        _threadUuid = getThreadUuid();
        attachment._workThreadManager.put(_threadUuid, Thread.currentThread());
    }

    /**
     * @Description Delete all caches of thread, like share map and socket channel
     * @param attachment M365_proxy_listen_connection shared class
     */
    private synchronized void destroy(M365_proxy_listen_connection attachment){
        try {
            boolean containKey = attachment._workThreadManager.containsKey(_threadUuid);
            if (containKey){
                attachment._workThreadManager.remove(_threadUuid);
            }

            _clientChannel.close();
            _clientChannel = null;
        } catch (IOException e){
            logger.warn("thread " + _threadUuid + "self destroy error: " + e.getMessage());
        }
    }

    private String getThreadUuid(){
        return Util.genericUuid();
    }
}
