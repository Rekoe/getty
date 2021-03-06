/**
 * <li>文件名：HttpCoder.java
 * <li>说明：
 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
 * <li>创建日期：2011-11-18
 * <li>修改人：
 * <li>修改日期：
 */
package com.gettyio.core.handler.codec.websocket;

import com.gettyio.core.buffer.AutoByteBuffer;
import com.gettyio.core.channel.AioChannel;
import com.gettyio.core.handler.codec.MessageToByteEncoder;
import com.gettyio.core.util.ObjectUtil;

/**
 * version 5+
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-------+-+-------------+-------------------------------+
 * |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
 * |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
 * |N|V|V|V|       |S|             |   (if payload len==126/127)   |
 * | |1|2|3|       |K|             |                               |
 * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 * |     Extended payload length continued, if payload len == 127  |
 * + - - - - - - - - - - - - - - - +-------------------------------+
 * |                               |Masking-key, if MASK set to 1  |
 * +-------------------------------+-------------------------------+
 * | Masking-key (continued)       |          Payload Data         |
 * +-------------------------------- - - - - - - - - - - - - - - - +
 * :                     Payload Data continued ...                :
 * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
 * |                     Payload Data continued ...                |
 * +---------------------------------------------------------------+
 * version 1-4
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-------+-+-------------+-------------------------------+
 * |M|R|R|R| opcode|R| Payload len |    Extended payload length    |
 * |O|S|S|S|  (4)  |S|     (7)     |             (16/63)           |
 * |R|V|V|V|       |V|             |   (if payload len==126/127)   |
 * |E|1|2|3|       |4|             |                               |
 * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 * |     Extended payload length continued, if payload len == 127  |
 * + - - - - - - - - - - - - - - - +-------------------------------+
 * |                               |         Extension data        |
 * +-------------------------------+ - - - - - - - - - - - - - - - +
 * :                                                               :
 * +---------------------------------------------------------------+
 * :                       Application data                        :
 * +---------------------------------------------------------------+
 * http协议响应编码器
 */
public class WebSocketEncoder extends MessageToByteEncoder {

    @Override
    public void encode(AioChannel aioChannel, Object obj) throws Exception {
        if (WebSocketDecoder.handShak) {
            byte[] bytes;
            if (obj instanceof String) {
                bytes = ((String) obj).getBytes();
            } else {
                bytes = (byte[]) obj;
            }
            if (Integer.valueOf(WebSocketDecoder.protocolVersion) <= WebSocketConstants.SPLITVERSION0) {
                String str = new String(bytes, "utf-8");
                String msg = (WebSocketConstants.BEGIN_MSG + str + WebSocketConstants.END_MSG);
                obj = msg.getBytes();
            } else {
                obj = codeVersion6(bytes);
            }
        }
        super.encode(aioChannel, obj);
    }


    /**
     * 方法名：codeVersion6
     *
     * @param msg byte[]
     * @return byte[]
     * 对websocket协议进行编码
     */
    public byte[] codeVersion6(byte[] msg) {

        AutoByteBuffer autoByteBuffer = AutoByteBuffer.newByteBuffer();
        WebSocketMessage messageFrame = new WebSocketMessage();
        messageFrame.setDateLength(msg.length);

        byte[] headers = new byte[2];
        headers[0] = WebSocketMessage.FIN;
        headers[0] |= messageFrame.getRsv1() | messageFrame.getRsv2() | messageFrame.getRsv3() | WebSocketMessage.TXT;
        headers[1] = 0;
        //headers[1] |=  messageFrame.getMask() | messageFrame.getPayloadLen();
        headers[1] |= 0x00 | messageFrame.getPayloadLen();
        // 头部控制信息
        autoByteBuffer.writeBytes(headers);

        if (messageFrame.getPayloadLen() == WebSocketMessage.HAS_EXTEND_DATA) {
            // 处理数据长度为126位的情况
            autoByteBuffer.writeBytes(ObjectUtil.shortToByte(messageFrame.getPayloadLenExtended()));
        } else if (messageFrame.getPayloadLen() == WebSocketMessage.HAS_EXTEND_DATA_CONTINUE) {
            // 处理数据长度为127位的情况
            autoByteBuffer.writeBytes(ObjectUtil.longToByte(messageFrame.getPayloadLenExtendedContinued()));
        }

        //写到客户端可以不做掩码处理
//        if (messageFrame.isMask()) {
//            // 做了掩码处理的，需要传递掩码的key
//            byte[] keys = messageFrame.getMaskingKey();
//            autoByteBuffer.writeBytes(messageFrame.getMaskingKey());
//
//            for (int i = 0; i < autoByteBuffer.array().length; ++i) {
        //进行掩码处理
//                autoByteBuffer.array()[i] ^= keys[i % 4];
//            }
//        }
        autoByteBuffer.writeBytes(msg);

        return autoByteBuffer.readableBytesArray();
    }


}
