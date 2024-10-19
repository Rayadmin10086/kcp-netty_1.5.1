/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure;

import io.netty.buffer.ByteBuf;
import java.util.List;
import io.jpower.kcp.netty.erasure.fec.FecPacket;

public interface IFecDecode {
    public List<ByteBuf> decode(FecPacket var1);

    public void release();
}

