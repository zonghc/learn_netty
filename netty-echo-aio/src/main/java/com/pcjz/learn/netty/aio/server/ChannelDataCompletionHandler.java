package com.pcjz.learn.netty.aio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <p>
 * 名称：数据传输回调<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 15:05
 * </p>
 */
class ChannelDataCompletionHandler implements CompletionHandler<Integer,ByteBuffer> {
    private AsynchronousSocketChannel socketChannel;//服务器与客户端之间的连接
    private boolean flag;//交互是否结束（是否需要关闭连接）：true-结束；false-正在交互；

    public ChannelDataCompletionHandler(AsynchronousSocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();//读取缓冲区前，需要重置
        String requestMsg = new String(buffer.array(),0,buffer.remaining());//读取缓冲区中的数据
        System.out.println("接收数据："+requestMsg);
        String responseMsg = requestMsg;
        if("close".equalsIgnoreCase(requestMsg)){
            this.flag = true;
        }
        response(responseMsg);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {
        System.out.println("接收数据失败，关闭连接！");
        close();
    }

    private void response(String responseMsg){
        ByteBuffer buffer = ByteBuffer.allocate(10);//开辟输出缓存区
        buffer.put(responseMsg.getBytes());
        buffer.flip();
        this.socketChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                if(buf.hasRemaining()){//如何输出缓存区中仍然有数据则继续输出
                    ChannelDataCompletionHandler.this.socketChannel.write(buf,buf,this);
                }else{
                    if(!flag){//如果当前交互还没有结束
                        ByteBuffer bf = ByteBuffer.allocate(10);//开辟数据传输缓冲区
                        //ChannelDataCompletionHandler.this.socketChannel.read(bf,bf,ChannelDataCompletionHandler.this);
                        ChannelDataCompletionHandler.this.socketChannel.read(bf,bf,
                                new ChannelDataCompletionHandler(ChannelDataCompletionHandler.this.socketChannel));
                    }
                }
            }

            @Override
            public void failed(Throwable e, ByteBuffer buf) {
                System.out.println("返回数据失败，关闭连接！");
                close();
            }
        });
    }

    //关闭连接
    private void close(){
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            System.out.println("关闭连接异常："+e.toString());
            e.printStackTrace();
        }
    }
}
