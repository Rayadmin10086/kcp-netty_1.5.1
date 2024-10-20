/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class ByteOutputInputTableCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
            for (int iOutput = 0; iOutput < outputCount; ++iOutput) {
                byte[] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; ++iInput) {
                    value ^= table[matrixRow[iInput] & 0xFF][inputs[iInput][iByte] & 0xFF];
                }
                outputs[iOutput][iByte] = (byte)value;
            }
        }
    }
}

