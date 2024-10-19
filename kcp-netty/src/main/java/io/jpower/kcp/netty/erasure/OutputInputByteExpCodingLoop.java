/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class OutputInputByteExpCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        for (int iOutput = 0; iOutput < outputCount; ++iOutput) {
            int iByte;
            byte[] outputShard = outputs[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            int iInput = 0;
            byte[] inputShard = inputs[0];
            byte matrixByte = matrixRow[0];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                outputShard[iByte] = Galois.multiply(matrixByte, inputShard[iByte]);
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
                matrixByte = matrixRow[iInput];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ Galois.multiply(matrixByte, inputShard[iByte]));
                }
            }
        }
    }
}

