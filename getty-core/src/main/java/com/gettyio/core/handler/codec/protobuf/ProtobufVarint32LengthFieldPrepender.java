package com.gettyio.core.handler.codec.protobuf;/*
 * 类名：ProtobufVarint32LengthFieldPrepender
 * 版权：Copyright by www.getty.com
 * 描述：
 * 修改人：gogym
 * 时间：2019/10/8
 */

import com.gettyio.core.buffer.AutoByteBuffer;
import com.gettyio.core.channel.AioChannel;
import com.gettyio.core.handler.codec.MessageToByteEncoder;

/**
 * 类名：ProtobufVarint32LengthFieldPrepender.java
 * 描述：
 * 修改人：gogym
 * 时间：2019/10/9
 */
public class ProtobufVarint32LengthFieldPrepender extends MessageToByteEncoder {

    @Override
    public void encode(AioChannel aioChannel, Object obj) throws Exception {
        byte[] bytes = (byte[]) obj;

        int bodyLen = bytes.length;
        int headerLen = computeRawVarint32Size(bodyLen);
        byte[] b = new byte[headerLen + bodyLen];

        AutoByteBuffer autoByteBuffer = AutoByteBuffer.newByteBuffer(bodyLen);
        writeRawVarint32(autoByteBuffer, bodyLen);
        autoByteBuffer.writeBytes(bytes);
        try {
            autoByteBuffer.readBytes(b);
        } catch (AutoByteBuffer.ByteBufferException e) {
            e.printStackTrace();
        }
        super.encode(aioChannel, b);
    }


    /**
     * Writes protobuf varint32 to (@link ByteBuf).
     *
     * @param out   to be written to
     * @param value to be written
     */
    static void writeRawVarint32(AutoByteBuffer out, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.write(value);
                return;
            } else {
                out.write((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }


    /**
     * Computes size of protobuf varint32 after encoding.
     *
     * @param value which is to be encoded.
     * @return size of value encoded as protobuf varint32.
     */
    static int computeRawVarint32Size(final int value) {
        if ((value & (0xffffffff << 7)) == 0) {
            return 1;
        }
        if ((value & (0xffffffff << 14)) == 0) {
            return 2;
        }
        if ((value & (0xffffffff << 21)) == 0) {
            return 3;
        }
        if ((value & (0xffffffff << 28)) == 0) {
            return 4;
        }
        return 5;
    }
}
