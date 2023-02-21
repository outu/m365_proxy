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

    /**
     * public values is shared by thread in attachment
     */
    public int _listen_port;

    public String _process_uuid;

    public boolean _stop = false;

    private int _thread_pool_num;

    private ExecutorService _service;

    private AsynchronousServerSocketChannel _serverChannel;

    /**
     * @Description init socket port
     * @param port socket port
     * @param uuid process uuid
     * @param threadPoolNum Socket listens to the number of threads in the asynchronous execution thread pool
     */
    public void init(int port, String uuid, int threadPoolNum){
        _listen_port = port;
        _process_uuid = uuid;
        _thread_pool_num = threadPoolNum;
    }

    /**
     * @Description Enable socket listening
     * @return true-success false-failed
     */
    public Boolean run() throws RuntimeException{
        System.out.println("server starting at port " +_listen_port + "..");
        // Initialize fixed length thread pool
        _service = Executors.newFixedThreadPool(_thread_pool_num);
        try {
            // Init AsynchronousServerSocketChannel
            _serverChannel = AsynchronousServerSocketChannel.open();
            _serverChannel.bind(new InetSocketAddress(_listen_port));
            // Listen to client connections, but in AIO, each accept can only receive one client,
            // so you need to call accept again in the processing logic to start the next listen, similar to chain call
            _serverChannel.accept(this, new M365_proxy_work_thread());
            while(true){
                if (_stop){
                    TimeUnit.SECONDS.sleep(5L);
                    M365_proxy_global_vals.g_service_exit_flag = true;
                    _serverChannel.close();
                    break;
                }
                TimeUnit.SECONDS.sleep(5L);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    public ExecutorService getService() {
        return _service;
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
    }
}
