/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class ByteInputOutputExpCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
            byte[] matrixRow;
            byte[] outputShard;
            int iOutput;
            int iInput = 0;
            byte[] inputShard = inputs[0];
            byte inputByte = inputShard[iByte];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                outputShard[iByte] = Galois.multiply(matrixRow[0], inputByte);
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
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

