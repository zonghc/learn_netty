package com.pcjz.learn.netty.bio.client;

import com.pcjz.learn.netty.util.InputUtil;
import com.pcjz.learn.netty.util.ServerInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * <p>
 * 名称：客户端<br/>
 * 描述：客户端<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/22 9:26
 * </p>
 */
public class BioEchoClient {

    public static final byte STATUS_CLOSED=0;//连接已关闭
    public static final byte STATUS_CONNECTED=1;//已连接
    public static final String CLOSE_COMMAND = "close";//关闭连接命令

    private Socket connection;//与服务器的连接
    private PrintStream sender;//向服务器发送信息
    private Scanner receiver;//接收服务器的响应
    private byte status = STATUS_CLOSED;

    public void getConnection(){
        status = STATUS_CLOSED;
        System.out.println("建立与服务器的连接");
        try {
            connection = new Socket(ServerInfo.HOST,ServerInfo.PORT);
            sender = new PrintStream(connection.getOutputStream());
            receiver = new Scanner(connection.getInputStream());
            receiver.useDelimiter("\n");
        } catch (IOException e) {
            System.out.println("建立与服务器的连接异常："+e.toString());
            e.printStackTrace();
            return;
        }
        status = STATUS_CONNECTED;
        sendAndReceive();
    }

    //收发数据
    private void sendAndReceive(){
        while (STATUS_CONNECTED == status){
            String sendMsg = InputUtil.getString("请输入请求数据：").trim();
            if(sendMsg != null && !"".equalsIgnoreCase(sendMsg)){
                this.sender.flush();
                this.sender.println(sendMsg);
            }
            String receiveMsg = null;
            if(this.receiver.hasNext()){
                receiveMsg = this.receiver.next().trim();
            }else{
                continue;
            }
            System.out.println("响应信息"+receiveMsg);
            if(CLOSE_COMMAND.equalsIgnoreCase(receiveMsg)){
                destroy();
                break;
            }
        }
    }

    //关闭连接、释放资源
    private void destroy(){
        System.out.println("客户端关闭连接、释放资源");
        if(this.connection !=null && !this.connection.isClosed()){
            try {
                this.connection.close();
            } catch (IOException e) {
                System.out.println("客户端关闭连接异常："+e.toString());
                e.printStackTrace();
            }
            this.connection = null;
        }

        if(this.sender != null){
            this.sender.flush();
            this.sender.close();
            this.sender = null;
        }
        if(this.receiver != null){
            this.receiver.close();
            this.receiver = null;
        }
    }

}
