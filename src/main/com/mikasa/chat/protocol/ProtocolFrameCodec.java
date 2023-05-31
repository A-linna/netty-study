package com.mikasa.chat.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author aiLun
 * @date 2023/5/30-17:12
 */
public class ProtocolFrameCodec extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameCodec(){
        this(1024, 12, 4, 0, 0);
    }

    public ProtocolFrameCodec(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
