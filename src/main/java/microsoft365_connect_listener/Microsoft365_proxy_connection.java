package microsoft365_connect_listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Microsoft365_proxy_connection {
    private ExecutorService service;

    public int stop = 0;

    private AsynchronousServerSocketChannel serverChannel;

    public ExecutorService getService() {
        return service;
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public Microsoft365_proxy_connection(int port) {
        init(port);
    }



    private void init(int port) {

        System.out.println("server starting at port "+port+"..");
        // 初始化定长线程池
        service = Executors.newFixedThreadPool(4);
        try {
            // 初始化 AsyncronousServersocketChannel
            serverChannel = AsynchronousServerSocketChannel.open();
//            // 监听端口

            serverChannel.bind(new InetSocketAddress(port));

            // 监听客户端连接,但在AIO，每次accept只能接收一个client，所以需要
            // 在处理逻辑种再次调用accept用于开启下一次的监听
            // 类似于链式调用
            //serverChannel.accept(this, new AioHandler());

            try {
                // 阻塞程序，防止被GC回收
                while(true){

                    if(stop == 1){
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
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getProperty("port"));
        new Microsoft365_proxy_connection(port);

        System.out.println("all finished!!!!!!");
    }
}
