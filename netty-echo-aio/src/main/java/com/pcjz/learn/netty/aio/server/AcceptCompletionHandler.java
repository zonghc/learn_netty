package com.pcjz.learn.netty.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * <p>
 * 名称：请求连接回调<br/>
 * 描述：服务器端与客户端建立连接后的回调<br/>
 * 作者: zonghc <br/>
 * 创建时间: 2018/11/23 13:54
 * </p>
 */
class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AioEchoServerThread> {

    /**
     *<p>
     *  名称：成功回调方法<br/>
     *  描述：请求连接建立成功的回调方法。
     *</p>
     *<p>
     *  @param socketChannel 监听并建立的与客户端的请求连接。
     *  @param serverThread 服务器通道线程，即serverSocketChannel.accept(this,new AcceptCompletionHandler())中的this。
     *</p>
     *<p>
     *  <hr/>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/23 14:38<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    @Override
    public void completed(AsynchronousSocketChannel socketChannel, AioEchoServerThread serverThread) {
        /*
        接收请求连接，与AioEchoServerThread中run方法的this.serverSocketChannel.accept(this,new AcceptCompletionHandler())形成闭环操作。
        */
        serverThread.getServerSocketChannel().accept(serverThread,this);//接收请求连接
        ByteBuffer buffer = ByteBuffer.allocate(10);//开辟数据传输缓冲区
        socketChannel.read(buffer,buffer,new ChannelDataCompletionHandler(socketChannel));//数据交互回调
    }

    /**
     *<p>
     *  名称：失败回调方法<br/>
     *  描述：请求连接建立失败的回调方法。
     *</p>
     *<p>
     *  @param e 异常对象
     *  @param serverThread 服务器通道线程，即serverSocketChannel.accept(this,new AcceptCompletionHandler())中的this。
     *</p>
     *<p>
     *  <hr/>
     *  创建人：zonghc <br/>
     *  创建时间：2018/11/23 14:42<br/>
     *  <hr/>
     *  修改人：<br/>
     *  修改时间：<br/>
     *  描述：
     *</p>
     */
    @Override
    public void failed(Throwable e, AioEchoServerThread serverThread) {

    }
}
