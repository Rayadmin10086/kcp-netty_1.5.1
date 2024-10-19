/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class OutputInputByteTableCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < outputCount; ++iOutput) {
            int iByte;
            byte[] outputShard = outputs[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            int iInput = 0;
            byte[] inputShard = inputs[0];
            byte[] multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                outputShard[iByte] = multTableRow[inputShard[iByte] & 0xFF];
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ multTableRow[inputShard[iByte] & 0xFF]);
                }
            }
        }
    }

    @Override
    public boolean checkSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] toCheck, int checkCount, int offset, int byteCount, byte[] tempBuffer) {
        if (tempBuffer == null) {
            return super.checkSomeShards(matrixRows, inputs, inputCount, toCheck, checkCount, offset, byteCount, null);
        }
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < checkCount; ++iOutput) {
            int iByte;
            byte[] outputShard = toCheck[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            int iInput = 0;
            byte[] inputShard = inputs[0];
            byte[] multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                tempBuffer[iByte] = multTableRow[inputShard[iByte] & 0xFF];
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    int n = iByte;
                    tempBuffer[n] = (byte)(tempBuffer[n] ^ multTableRow[inputShard[iByte] & 0xFF]);
                }
            }
            for (int iByte2 = offset; iByte2 < offset + byteCount; ++iByte2) {
                if (tempBuffer[iByte2] == outputShard[iByte2]) continue;
                return false;
            }
        }
        return true;
    }
}

