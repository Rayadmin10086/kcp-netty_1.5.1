/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.ByteInputOutputExpCodingLoop;
import io.jpower.kcp.netty.erasure.ByteInputOutputTableCodingLoop;
import io.jpower.kcp.netty.erasure.ByteOutputInputExpCodingLoop;
import io.jpower.kcp.netty.erasure.ByteOutputInputTableCodingLoop;
import io.jpower.kcp.netty.erasure.InputByteOutputExpCodingLoop;
import io.jpower.kcp.netty.erasure.InputByteOutputTableCodingLoop;
import io.jpower.kcp.netty.erasure.InputOutputByteExpCodingLoop;
import io.jpower.kcp.netty.erasure.InputOutputByteTableCodingLoop;
import io.jpower.kcp.netty.erasure.OutputByteInputExpCodingLoop;
import io.jpower.kcp.netty.erasure.OutputByteInputTableCodingLoop;
import io.jpower.kcp.netty.erasure.OutputInputByteExpCodingLoop;
import io.jpower.kcp.netty.erasure.OutputInputByteTableCodingLoop;

public interface CodingLoop {
    public static final CodingLoop[] ALL_CODING_LOOPS = new CodingLoop[]{new ByteInputOutputExpCodingLoop(), new ByteInputOutputTableCodingLoop(), new ByteOutputInputExpCodingLoop(), new ByteOutputInputTableCodingLoop(), new InputByteOutputExpCodingLoop(), new InputByteOutputTableCodingLoop(), new InputOutputByteExpCodingLoop(), new InputOutputByteTableCodingLoop(), new OutputByteInputExpCodingLoop(), new OutputByteInputTableCodingLoop(), new OutputInputByteExpCodingLoop(), new OutputInputByteTableCodingLoop()};

    public void codeSomeShards(byte[][] var1, byte[][] var2, int var3, byte[][] var4, int var5, int var6, int var7);

    public boolean checkSomeShards(byte[][] var1, byte[][] var2, int var3, byte[][] var4, int var5, int var6, int var7, byte[] var8);
}

