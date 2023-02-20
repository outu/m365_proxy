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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class M365_proxy_listen_connection {

    public int _listen_port;

    public static String _process_uuid;

    private ExecutorService service;

    private AsynchronousServerSocketChannel serverChannel;

    public void init(int port, String uuid){
        _listen_port = port;
        _process_uuid = uuid;

    }

    public Boolean run(){
        System.out.println("server starting at port " +_listen_port + "..");
        // 初始化定长线程池
        service = Executors.newFixedThreadPool(4);
        try {
            // 初始化 AsyncronousServersocketChannel
            serverChannel = AsynchronousServerSocketChannel.open();
//            // 监听端口

            serverChannel.bind(new InetSocketAddress(_listen_port));

            // 监听客户端连接,但在AIO，每次accept只能接收一个client，所以需要
            // 在处理逻辑种再次调用accept用于开启下一次的监听

            // 类似于链式调用
            serverChannel.accept(this, new M365_proxy_work_thread());

            try {
                // 阻塞程序，防止被GC回收
                while(true){

                    if(M365_proxy_global_vals.g_service_exit_flag){
                        serverChannel.close();
                        break;
                    }

                    TimeUnit.SECONDS.sleep(5L);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public ExecutorService getService() {
        return service;
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public void destroy() throws IOException {
        serverChannel.close();
    }
}
