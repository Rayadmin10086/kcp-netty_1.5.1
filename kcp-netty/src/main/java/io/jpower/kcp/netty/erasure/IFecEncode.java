package io.jpower.kcp.netty.erasure;

import io.netty.buffer.ByteBuf;

public interface IFecEncode {
    public ByteBuf[] encode(ByteBuf var1);

    public void release();
}

