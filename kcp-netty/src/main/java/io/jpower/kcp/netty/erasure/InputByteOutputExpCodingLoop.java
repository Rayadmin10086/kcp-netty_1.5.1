/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class InputByteOutputExpCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[] matrixRow;
        byte[] outputShard;
        int iOutput;
        byte inputByte;
        int iByte;
        int iInput = 0;
        byte[] inputShard = inputs[0];
        for (iByte = offset; iByte < offset + byteCount; ++iByte) {
            inputByte = inputShard[iByte];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                outputShard[iByte] = Galois.multiply(matrixRow[0], inputByte);
            }
        }
        for (iInput = 1; iInput < inputCount; ++iInput) {
            inputShard = inputs[iInput];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                inputByte = inputShard[iByte];
                for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                    outputShard = outputs[iOutput];
                    matrixRow = matrixRows[iOutput];
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ Galois.multiply(matrixRow[iInput], inputByte));
                }
            }
        }
    }
}

