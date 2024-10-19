/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure;

import io.netty.buffer.ByteBuf;

public interface IFecEncode {
    public ByteBuf[] encode(ByteBuf var1);

    public void release();
}

