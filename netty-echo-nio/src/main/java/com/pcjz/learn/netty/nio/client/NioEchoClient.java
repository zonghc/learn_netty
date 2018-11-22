package com.pcjz.learn.netty.nio.client;

import com.pcjz.learn.netty.util.InputUtil;
import com.pcjz.learn.netty.util.ServerInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <p>
 * 名称：客户端<br/>
 * 描述：客户端<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/22 9:26
 * </p>
 */
public class NioEchoClient {

    public static final byte STATUS_CLOSED=0;//连接已关闭
    public static final byte STATUS_CONNECTED=1;//已连接
    public static final String CLOSE_COMMAND = "close";//关闭连接命令

    private SocketChannel connection;//与服务器的连接
    private byte status = STATUS_CLOSED;

    public void getConnection(){
        this.status = STATUS_CLOSED;
        System.out.println("建立与服务器的连接");
        try {
            connection = SocketChannel.open();
            connection.connect(new InetSocketAddress(ServerInfo.HOST,ServerInfo.PORT));
        } catch (Exception e) {
            System.out.println("建立与服务器的连接异常："+e.toString());
            e.printStackTrace();
            return;
        }
        this.status = STATUS_CONNECTED;
        sendAndReceive();
    }

    //收发数据
    private void sendAndReceive(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            while (STATUS_CONNECTED == this.status){
                String sendMsg = InputUtil.getString("请输入请求数据：").trim();
                buffer.clear();
                buffer.put(sendMsg.getBytes());
                buffer.flip();
                this.connection.write(buffer);
                buffer.clear();
                int length = this.connection.read(buffer);
                String receiveMsg = new String(buffer.array(),0,length);
                System.out.println("响应信息"+receiveMsg);
                if(CLOSE_COMMAND.equalsIgnoreCase(receiveMsg)){
                    destroy();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("收发数据异常："+e.toString());
            e.printStackTrace();
            destroy();
        }
    }

    //关闭连接、释放资源
    private void destroy(){
        System.out.println("客户端关闭连接、释放资源");
        if(this.connection !=null){
            try {
                this.connection.close();
            } catch (Exception e) {
                System.out.println("客户端关闭连接异常："+e.toString());
                e.printStackTrace();
            }
            this.connection = null;
        }
    }

}
