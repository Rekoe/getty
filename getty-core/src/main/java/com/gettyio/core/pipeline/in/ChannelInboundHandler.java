/**
 * 包名：org.getty.core.pipeline.in
 * 版权：Copyright by www.getty.com
 * 描述：
 * 邮箱：189155278@qq.com
 * 时间：2019/9/27
 */
package com.gettyio.core.pipeline.in;


import com.gettyio.core.channel.AioChannel;
import com.gettyio.core.pipeline.ChannelboundHandler;
import com.gettyio.core.util.LinkedNonBlockQueue;


/**
 * 类名：ChannelInboundHandler.java
 * 描述：
 * 修改人：gogym
 * 时间：2019/9/27
 */
public interface ChannelInboundHandler extends ChannelboundHandler {

    /**
     * 连接
     *
     * @param aioChannel 通道
     * @throws Exception 异常
     */
    void channelAdded(AioChannel aioChannel) throws Exception;

    /**
     * 连接关闭
     *
     * @param aioChannel 通道
     * @throws Exception 异常
     */
    void channelClosed(AioChannel aioChannel) throws Exception;

    /**
     * 消息读取
     *
     * @param obj        读取消息
     * @param aioChannel 通道
     * @throws Exception 异常
     */
    void channelRead(AioChannel aioChannel, Object obj) throws Exception;

    /**
     * 异常
     *
     * @param aioChannel 通道
     * @param cause      异常信息
     * @throws Exception 异常
     */
    void exceptionCaught(AioChannel aioChannel, Throwable cause) throws Exception;

    /**
     * 消息解码
     *
     * @param aioChannel 通道
     * @param obj        消息
     * @param out 消息队列
     * @throws Exception 异常
     */
    void decode(AioChannel aioChannel, Object obj, LinkedNonBlockQueue<Object> out) throws Exception;

}
