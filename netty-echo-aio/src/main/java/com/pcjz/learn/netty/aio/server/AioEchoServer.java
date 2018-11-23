package com.pcjz.learn.netty.aio.server;

import com.pcjz.learn.netty.util.ServerInfo;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 13:14
 * </p>
 */
public class AioEchoServer {
    public void startup(){
        new AioEchoServerThread(ServerInfo.PORT).run();
    }
}
