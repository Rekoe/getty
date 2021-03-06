package com.gettyio.string.tcp;


import com.gettyio.core.channel.AioChannel;
import com.gettyio.core.pipeline.in.SimpleChannelInboundHandler;

public class SimpleHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelAdded(AioChannel aioChannel) {

        System.out.println("连接成功");

    }

    @Override
    public void channelClosed(AioChannel aioChannel) {
        System.out.println("连接关闭了");
    }


    @Override
    public void channelRead0(AioChannel aioChannel, String str) {

        System.out.println("读取消息了:" + str);
    }

    @Override
    public void exceptionCaught(AioChannel aioChannel, Throwable cause) throws Exception {
        System.out.println("出错了");
    }
}
