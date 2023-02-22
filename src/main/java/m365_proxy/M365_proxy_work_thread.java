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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class M365_proxy_work_thread implements CompletionHandler<AsynchronousSocketChannel, M365_proxy_listen_connection> {
    private AsynchronousSocketChannel _clientChannel;
    private M365_proxy_rpc_server _rpcServer;

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, M365_proxy_listen_connection attachment) {
        //start the next listen
        attachment.getServerChannel().accept(attachment, this);

        _clientChannel = socketChannel;
        _rpcServer = new M365_proxy_rpc_server();
        _rpcServer.init(socketChannel, attachment);
        _rpcServer.waitAndHandleRequest();
        _rpcServer = null;
    }

    @Override
    public void failed(Throwable exc, M365_proxy_listen_connection attachment) {
        //exc.printStackTrace();
        if (_rpcServer != null){
            _rpcServer = null;
        }
        if (_clientChannel != null && _clientChannel.isOpen() && attachment != null){
            attachment.getServerChannel().accept(attachment, this);
        }
    }

    private void destroy(){

    }
}
