package com.pcjz.learn.netty.aio;

import com.pcjz.learn.netty.aio.server.AioEchoServer;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 15:51
 * </p>
 */
public class StartServer {

    public static void main(String[] args) {
        new AioEchoServer().startup();
    }
}
