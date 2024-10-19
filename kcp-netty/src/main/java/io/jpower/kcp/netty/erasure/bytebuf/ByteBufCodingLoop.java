/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure.bytebuf;

import io.netty.buffer.ByteBuf;

public interface ByteBufCodingLoop {
    public void codeSomeShards(byte[][] var1, ByteBuf[] var2, int var3, ByteBuf[] var4, int var5, int var6, int var7);

    public boolean checkSomeShards(byte[][] var1, ByteBuf[] var2, int var3, byte[][] var4, int var5, int var6, int var7, byte[] var8);
}

