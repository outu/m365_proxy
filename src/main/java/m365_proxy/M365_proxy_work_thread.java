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

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class M365_proxy_work_thread implements CompletionHandler<AsynchronousSocketChannel, M365_proxy_listen_connection> {
    @Override
    public void completed(AsynchronousSocketChannel result, M365_proxy_listen_connection attachment) {

    }

    @Override
    public void failed(Throwable exc, M365_proxy_listen_connection attachment) {

    }
}
