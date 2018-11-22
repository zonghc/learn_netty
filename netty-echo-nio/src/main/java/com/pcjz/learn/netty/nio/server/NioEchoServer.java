package com.pcjz.learn.netty.nio.server;

import com.pcjz.learn.netty.util.ServerInfo;

import java.io.IOException;
import java.net.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 名称：服务器<br/>
 * 描述：基于NIO数据传输的Echo应用服务器。<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/22 13:48
 * </p>
 */
public class NioEchoServer {

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
    private static ServerSocketChannel serverSocketChannel = null;
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
        System.out.println("Nio echo server starting...");
        try {
            //1、NIO的处理是基于channel（通道）的，所以需要先打开channel。
            serverSocketChannel = ServerSocketChannel.open();
            //2、需要为channel设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);//设置非阻塞模式
            //3、服务器上需要提供一个网络的监听端口（监听地址和端口）
            serverSocketChannel.bind(new InetSocketAddress(ServerInfo.PORT));
            //4、NIO中所有的channel都由一个对应的Selector对其进行管理。
            //  因此，需要设置一个Selector，作为选择器，用于管理所有的Channel。
            Selector selector = Selector.open();
            //5、将当期的Channel注册给selector。
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//连接时处理
            setStatus(STATUS_STARTED);
            System.out.println("Bio echo server started successfully.");
            /*
             服务器正常工作时，接收并处理用户请求。
             */
            //6、NIO采用的是轮训模式，每当发现有用户连接的时候就要启动一个线程处理（线程池）
            int keySelect = 0;//接收轮询状态
            while ((keySelect = selector.select())>0 && STATUS_INITING == getStatus()){//轮询处理
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//获取全部的key
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()){//遍历所有的key
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()){//如果key为连接模式则建立连接进行通信
                        SocketChannel socketChannel = serverSocketChannel.accept();//建立连接
                        if(socketChannel != null){
                            fixedThreadPool.submit(new NioEchoClientHandler(socketChannel));
                        }
                    }
                    iterator.remove();
                }
            }
            shutDown();
        } catch (IOException e) {
            setStatus(STATUS_INITING);
            shutDown();
            System.out.println("Nio echo server started failure with exception："+e.toString());
            e.printStackTrace();
            return;
        }
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
            serverSocketChannel.close();
        } catch (IOException e) {
            System.out.println("Nio echo server close exception："+e.toString());
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
