/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class ByteInputOutputTableCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
            byte[] multTableRow;
            byte[] matrixRow;
            byte[] outputShard;
            int iOutput;
            int iInput = 0;
            byte[] inputShard = inputs[0];
            byte inputByte = inputShard[iByte];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                multTableRow = table[matrixRow[0] & 0xFF];
                outputShard[iByte] = multTableRow[inputByte & 0xFF];
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
                inputByte = inputShard[iByte];
                for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                    outputShard = outputs[iOutput];
                    matrixRow = matrixRows[iOutput];
                    multTableRow = table[matrixRow[iInput] & 0xFF];
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ multTableRow[inputByte & 0xFF]);
                }
            }
        }
    }
}

