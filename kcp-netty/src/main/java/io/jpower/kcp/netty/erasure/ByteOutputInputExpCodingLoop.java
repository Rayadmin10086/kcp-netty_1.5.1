/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class ByteOutputInputExpCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
            for (int iOutput = 0; iOutput < outputCount; ++iOutput) {
                byte[] matrixRow = matrixRows[iOutput];
                int value = 0;
                for (int iInput = 0; iInput < inputCount; ++iInput) {
                    value ^= Galois.multiply(matrixRow[iInput], inputs[iInput][iByte]);
                }
                outputs[iOutput][iByte] = (byte)value;
            }
        }
    }
}

