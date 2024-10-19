/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure.bytebuf;

import io.netty.buffer.ByteBuf;
import io.jpower.kcp.netty.erasure.Galois;
import io.jpower.kcp.netty.erasure.bytebuf.ByteBufCodingLoop;

public abstract class ByteBufCodingLoopBase
implements ByteBufCodingLoop {
    @Override
    public boolean checkSomeShards(byte[][] matrixRows, ByteBuf[] inputs, int inputCount, byte[][] toCheck, int checkCount, int offset, int byteCount, byte[] tempBuffer) {
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
            for (int iOutput = 0; iOutput < checkCount; ++iOutput) {
                byte[] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; ++iInput) {
                    value ^= table[matrixRow[iInput] & 0xFF][inputs[iInput].getByte(iByte) & 0xFF];
                }
                if (toCheck[iOutput][iByte] == (byte)value) continue;
                return false;
            }
        }
        return true;
    }
}

