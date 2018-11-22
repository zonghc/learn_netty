package com.pcjz.learn.netty.bio.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

/**
 * <p>
 * 名称：保持客户端连接<br/>
 * 描述：保持并处理客户端连接。<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/21 20:39
 * </p>
 */
class EchoClientHandler implements Runnable{
    public static byte STATUS_CLOSED = 0;//连接已关闭
    public static byte STATUS_CONNECTED = 1;//已连接
    public static final String CLOSE_COMMAND = "close";//关闭连接命令

    private Socket client ;//客户端任务（task）。每个客户端都需要启动一个任务来执行操作
    private Scanner request;
    private PrintStream response ;
    private byte status = STATUS_CONNECTED;//任务状态
    private String id;//任务id

    protected EchoClientHandler(Socket client){
        this.client = client;
        this.status = STATUS_CONNECTED;
        this.id = UUID.randomUUID().toString();
        try {
            System.out.println("保持客户端连接，建立读写通道【"+this.id+"】");
            this.request = new Scanner(this.client.getInputStream());
            this.request.useDelimiter("\n");//设置换行符为分隔符
            this.response = new PrintStream(this.client.getOutputStream());
        } catch (IOException e) {
            System.out.println("保持客户端连接，建立读写通道异常："+e.toString());
            e.printStackTrace();
            this.status = STATUS_CLOSED;
        }
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
        while (STATUS_CONNECTED == status){
            String requestMsg = null;
            if(this.request.hasNext()){
                requestMsg = this.request.next();
            }else{
                continue;
            }
            System.out.println("【"+this.id+"】"+requestMsg);
            if(CLOSE_COMMAND.equalsIgnoreCase(requestMsg)){
                System.out.println("【"+this.id+"】go to close.");
                this.response.println(requestMsg);
                close();
            }else{
                this.response.println(requestMsg);
            }
        }
    }

    /**
     *<p>
     *  名称：关闭客户端<br/>
     *  描述：关闭与客户端的连接。
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/22 8:34<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    protected void close(){
        this.status = STATUS_CLOSED;
        if(this.response != null){
            this.response.flush();
            this.response.close();
        }
        if(this.request != null){
            this.request.close();
        }
        if(this.client != null && !this.client.isClosed()){
            try {
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}