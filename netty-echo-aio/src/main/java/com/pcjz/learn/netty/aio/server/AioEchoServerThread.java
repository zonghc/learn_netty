package com.pcjz.learn.netty.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * 名称：服务器通道线程<br/>
 * 描述：开启服务器通道，并建立与客户端的连接。每个线程开启一个通道（监听一个端口）。<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 13:15
 * </p>
 */
class AioEchoServerThread implements Runnable{
    private CountDownLatch latch;//做一个同步处理
    private AsynchronousServerSocketChannel serverSocketChannel;//服务器通道
    private int port = 8080;//默认监听8080端口

    public AioEchoServerThread(){
        this(8080);
    }

    public AioEchoServerThread(int port){
        try {
            this.port = port;
            System.out.println("打开服务器通道，端口【："+this.port+"】");
            this.latch = new CountDownLatch(1);//等待线程数量为1
            this.serverSocketChannel = AsynchronousServerSocketChannel.open();//打开服务器通道
            this.serverSocketChannel.bind(new InetSocketAddress(this.port));
            System.out.println("服务器通道打开成功，端口【"+this.port+"】");
        } catch (IOException e) {
            System.out.println("打开服务器通道，端口【："+this.port+"】异常："+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.serverSocketChannel.accept(this,new AcceptCompletionHandler());//接收请求连接
        try {
            this.latch.await();//等待请求连接
        } catch (InterruptedException e) {
            System.out.println("通道【："+this.port+"】等待请求连接异常："+e.toString());
            e.printStackTrace();
        }
        System.out.println("服务器通道结束"+this.port);
    }

    public int getPort() {
        return port;
    }
    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }
}
