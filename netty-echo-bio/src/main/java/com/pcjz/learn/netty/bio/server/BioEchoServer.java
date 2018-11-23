package com.pcjz.learn.netty.bio.server;

import com.pcjz.learn.netty.util.ServerInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/21 19:04
 * </p>
 */
public class BioEchoServer {
    /**
     *  服务器工作状态:初始化中
     */
    public static final byte STATUS_INITING = 0;
    /**
     *  服务器工作状态:启动中
     */
    public static final byte STATUS_STARTING = 1;
    /**
     *  服务器工作状态:已启动成功，正在正常工作
     */
    public static final byte STATUS_STARTED = 2;
    /**
     *  服务器工作状态:停止中，不再接收请求
     */
    public static final byte STATUS_STOPING = 3;
    /**
     *  服务器工作状态:已停止
     */
    public static final byte STATUS_STOPED = 4;
    /**
     *  服务器同时受理的最大用户数
     */
    public static final int MAX_CLIENT = 3;
    /**
     *  服务器
     */
    private static ServerSocket SERVERSOCKET = null;
    /**
     *  服务器线程池
     */
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(MAX_CLIENT);
    /**
     *  服务器工作状态
     */
    private static byte status = STATUS_INITING;

    /**
     *<p>
     *  名称：启动服务器<br/>
     *  描述：启动服务器端socket。
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/21 20:17<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    public static void startUp(){
        setStatus(STATUS_INITING);
        System.out.println("Bio echo server starting...");
        try {
            setStatus(STATUS_STARTING);
            SERVERSOCKET = new ServerSocket(ServerInfo.PORT);
            setStatus(STATUS_STARTED);
            System.out.println("Bio echo server started successfully.");
        } catch (IOException e) {
            setStatus(STATUS_INITING);
            System.out.println("Bio echo server started failure with exception："+e.toString());
            e.printStackTrace();
            return;
        }
        /*
         服务器正常工作时，接收并处理用户请求。
         */
        while (STATUS_INITING == getStatus()){
            try {
                Socket socket = SERVERSOCKET.accept();
                fixedThreadPool.submit(new EchoClientHandler(socket));
            } catch (IOException e) {
                System.out.println("接收并处理用户请求异常："+e.toString());
                e.printStackTrace();
                shutDown();
            }
        }
        shutDown();
    }

    /**
     *<p>
     *  名称：关闭服务器<br/>
     *  描述：关闭服务器端socket。
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/21 20:17<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    public static void shutDown(){
        fixedThreadPool.shutdown();
        try {
            if(!SERVERSOCKET.isClosed()){
                SERVERSOCKET.close();
            }
        } catch (IOException e) {
            System.out.println("Bio echo server close exception："+e.toString());
            e.printStackTrace();
        }
    }

    /**
     *<p>
     *  名称：获取服务器状态<br/>
     *  描述：返回服务器状态码。
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/22 7:45<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    public static byte getStatus(){
        return status;
    }

    /**
     *<p>
     *  名称：设置服务器状态<br/>
     *  描述：设置服务器状态，如果要设置的状态码不存在，则直接设置为初始状态。
     *</p>
     *<p>
     *  @param status 要设置的状态码
     *  @return void
     *</p>
     *<p>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/22 7:51<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    private static void setStatus(Byte status){
        switch (status){
            case STATUS_INITING:
                status=STATUS_INITING;
                break;
            case STATUS_STARTING:
                status=STATUS_STARTING;
                break;
            case STATUS_STARTED:
                status=STATUS_STARTED;
                break;
            case STATUS_STOPING:
                status=STATUS_STOPING;
                break;
            case STATUS_STOPED:
                status=STATUS_STOPED;
                break;
            default:
                status=STATUS_INITING;
                break;
        }
    }

}
