/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class InputByteOutputTableCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[] matrixRow;
        byte[] outputShard;
        int iOutput;
        byte[] multTableRow;
        byte inputByte;
        int iByte;
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        int iInput = 0;
        byte[] inputShard = inputs[0];
        for (iByte = offset; iByte < offset + byteCount; ++iByte) {
            inputByte = inputShard[iByte];
            multTableRow = table[inputByte & 0xFF];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                outputShard[iByte] = multTableRow[matrixRow[0] & 0xFF];
            }
        }
        for (iInput = 1; iInput < inputCount; ++iInput) {
            inputShard = inputs[iInput];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                inputByte = inputShard[iByte];
                multTableRow = table[inputByte & 0xFF];
                for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                    outputShard = outputs[iOutput];
                    matrixRow = matrixRows[iOutput];
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ multTableRow[matrixRow[iInput] & 0xFF]);
                }
            }
        }
    }
}

