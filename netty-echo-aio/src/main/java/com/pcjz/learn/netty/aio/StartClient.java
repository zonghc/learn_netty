package com.pcjz.learn.netty.aio;

import com.pcjz.learn.netty.aio.client.AioEchoClient;

/**
 * <p>
 * 名称：<br/>
 * 描述：<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 16:37
 * </p>
 */
public class StartClient {
    public static void main(String[] args) {
        new AioEchoClient().startup();
    }
}
