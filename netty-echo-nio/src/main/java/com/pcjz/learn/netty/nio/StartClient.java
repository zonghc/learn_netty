package com.pcjz.learn.netty.nio;

import com.pcjz.learn.netty.nio.client.NioEchoClient;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/22 10:29
 * </p>
 */
public class StartClient {
    public static void main(String[] args) {
        NioEchoClient nioEchoClient = new NioEchoClient();
        nioEchoClient.getConnection();
    }

}
