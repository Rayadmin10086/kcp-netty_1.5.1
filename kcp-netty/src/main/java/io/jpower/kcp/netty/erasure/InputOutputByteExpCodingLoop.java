/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class InputOutputByteExpCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        int iByte;
        byte matrixByte;
        byte[] matrixRow;
        byte[] outputShard;
        int iOutput;
        int iInput = 0;
        byte[] inputShard = inputs[0];
        for (iOutput = 0; iOutput < outputCount; ++iOutput) {
            outputShard = outputs[iOutput];
            matrixRow = matrixRows[iOutput];
            matrixByte = matrixRow[0];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                outputShard[iByte] = Galois.multiply(matrixByte, inputShard[iByte]);
            }
        }
        for (iInput = 1; iInput < inputCount; ++iInput) {
            inputShard = inputs[iInput];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                matrixByte = matrixRow[iInput];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ Galois.multiply(matrixByte, inputShard[iByte]));
                }
            }
        }
    }
}

