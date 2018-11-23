package com.pcjz.learn.netty.aio.client;

import com.pcjz.learn.netty.util.InputUtil;
import com.pcjz.learn.netty.util.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 15:52
 * </p>
 */
public class AioEchoClient {
    public void startup(){
        AioClientThread client = new AioClientThread();
        new Thread(client).start();
        while (client.send(InputUtil.getString("请输入要发送的数据："))){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class AioClientThread implements Runnable{
    private AsynchronousSocketChannel socketChannel;//连接
    private CountDownLatch latch;
    public AioClientThread(){
        System.out.println("连接服务器");
        try {
            this.socketChannel = AsynchronousSocketChannel.open();
            this.socketChannel.connect(new InetSocketAddress(ServerInfo.HOST,ServerInfo.PORT));
            this.latch = new CountDownLatch(1);
        } catch (IOException e) {
            System.out.println("连接服务器异常："+e.toString());
            e.printStackTrace();
        }
        System.out.println("连接服务器成功！");
    }

    @Override
    public void run() {
        try {
            this.latch.await();//等待数据交互
        } catch (InterruptedException e) {
            System.out.println("等待数据交互异常："+e.toString());
            e.printStackTrace();
        }
        System.out.println("客户端结束");
    }

    //发送数据
    public boolean send(String sendMsg){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(sendMsg.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer,buffer,new sendCompletionHandler(this.socketChannel,this.latch));
        if("close".equalsIgnoreCase(sendMsg)){
            this.latch.countDown();
            return false;
        }else{
            return true;
        }
    }
}
class sendCompletionHandler implements CompletionHandler<Integer,ByteBuffer>{
    private AsynchronousSocketChannel socketChannel;
    private CountDownLatch latch;
    public sendCompletionHandler(AsynchronousSocketChannel socketChannel,CountDownLatch latch){
        this.socketChannel = socketChannel;
        this.latch = latch;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()){//如果有待发送数据则进行发送
            this.socketChannel.write(buffer,buffer,this);
        }else{//如果没有待发送数据，则进行读取
            ByteBuffer readbuffer = ByteBuffer.allocate(10);
            this.socketChannel.read(readbuffer,readbuffer,new ReceiveCompletionHandl(this.socketChannel,this.latch));
        }
    }

    @Override
    public void failed(Throwable e, ByteBuffer buffer) {
        System.out.println("向服务器发送数据失败！");
        try {
            this.socketChannel.close();
        } catch (IOException e1) {
            System.out.println("客户端关闭连接异常："+e1.toString());
            e1.printStackTrace();
        }
        this.latch.countDown();
    }
}

class ReceiveCompletionHandl implements CompletionHandler<Integer,ByteBuffer>{
    private AsynchronousSocketChannel socketChannel;
    private CountDownLatch latch;
    public ReceiveCompletionHandl(AsynchronousSocketChannel socketChannel,CountDownLatch latch){
        this.socketChannel = socketChannel;
        this.latch = latch;
    }
    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String receiveMsg = new String(buffer.array(),0,buffer.remaining());
        System.out.println("响应数据："+receiveMsg);
    }
    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {
        System.out.println("接收服务器响应数据失败！");
        try {
            this.socketChannel.close();
        } catch (IOException e2) {
            System.out.println("客户端关闭连接异常："+e2.toString());
            e2.printStackTrace();
        }
        this.latch.countDown();
    }
}