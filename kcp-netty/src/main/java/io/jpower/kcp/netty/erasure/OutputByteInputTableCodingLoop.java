/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import io.jpower.kcp.netty.erasure.CodingLoopBase;
import io.jpower.kcp.netty.erasure.Galois;

public class OutputByteInputTableCodingLoop
extends CodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, byte[][] inputs, int inputCount, byte[][] outputs, int outputCount, int offset, int byteCount) {
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < outputCount; ++iOutput) {
            byte[] outputShard = outputs[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            for (int iByte = offset; iByte < offset + byteCount; ++iByte) {
                int value = 0;
                for (int iInput = 0; iInput < inputCount; ++iInput) {
                    byte[] inputShard = inputs[iInput];
                    byte[] multTableRow = table[matrixRow[iInput] & 0xFF];
                    value ^= multTableRow[inputShard[iByte] & 0xFF];
                }
                outputShard[iByte] = (byte)value;
            }
        }
    }
}

