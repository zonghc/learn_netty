package com.pcjz.learn.netty.nio.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/22 16:08
 * </p>
 */
class NioEchoClientHandler implements Runnable{
    public static byte STATUS_CLOSED = 0;//连接已关闭
    public static byte STATUS_CONNECTED = 1;//已连接
    public static final String CLOSE_COMMAND = "close";//关闭连接命令

    private SocketChannel clientChannel;//客户端任务（task）。每个客户端都需要启动一个任务来执行操作
    private String id;//任务id
    private byte status = STATUS_CONNECTED;//任务状态

    protected NioEchoClientHandler(SocketChannel clientChannel){
        this.id = UUID.randomUUID().toString();
        this.status = STATUS_CONNECTED;
        this.clientChannel = clientChannel;
    }

    /**
     *<p>
     *  名称：执行客户端任务<br/>
     *  描述：执行客户端任务。处理客户端的请求并返回信息。
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/22 8:33<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            while (STATUS_CONNECTED == status){
                buffer.clear();

                 int length = this.clientChannel.read(buffer);
                 String requestMsg = new String(buffer.array(),0,length).trim();
                 System.out.println("【"+this.id+"】[收到并返回]"+requestMsg);
                 String responseMsg = requestMsg;
                 if(CLOSE_COMMAND.equalsIgnoreCase(requestMsg)){
                     System.out.println("【"+this.id+"】go to close.");
                     this.status = STATUS_CLOSED;
                 }
                 buffer.clear();
                 buffer.put(responseMsg.getBytes());
                 buffer.flip();
                 this.clientChannel.write(buffer);
            }
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
