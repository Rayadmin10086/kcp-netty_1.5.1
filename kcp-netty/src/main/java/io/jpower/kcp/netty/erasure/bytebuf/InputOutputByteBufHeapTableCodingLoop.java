/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure.bytebuf;

import io.netty.buffer.ByteBuf;
import io.jpower.kcp.netty.erasure.Galois;
import io.jpower.kcp.netty.erasure.bytebuf.ByteBufCodingLoopBase;

public class InputOutputByteBufHeapTableCodingLoop
extends ByteBufCodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, ByteBuf[] inputs, int inputCount, ByteBuf[] outputs, int outputCount, int offset, int byteCount) {
        int iByte;
        byte[] multTableRow;
        byte[] matrixRow;
        int iOutput;
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        int count = offset + byteCount;
        byte[] inputShard = new byte[count];
        byte[] outputShard = new byte[count];
        int iInput = 0;
        inputs[0].getBytes(0, inputShard);
        for (iOutput = 0; iOutput < outputCount; ++iOutput) {
            outputs[iOutput].getBytes(0, outputShard);
            matrixRow = matrixRows[iOutput];
            multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < count; ++iByte) {
                outputShard[iByte] = multTableRow[inputShard[iByte] & 0xFF];
            }
            outputs[iOutput].setBytes(0, outputShard);
        }
        for (iInput = 1; iInput < inputCount; ++iInput) {
            inputs[iInput].getBytes(0, inputShard);
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputs[iOutput].getBytes(0, outputShard);
                matrixRow = matrixRows[iOutput];
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < count; ++iByte) {
                    int n = iByte;
                    outputShard[n] = (byte)(outputShard[n] ^ multTableRow[inputShard[iByte] & 0xFF]);
                }
                outputs[iOutput].setBytes(0, outputShard);
            }
        }
    }

    @Override
    public boolean checkSomeShards(byte[][] matrixRows, ByteBuf[] inputs, int inputCount, byte[][] toCheck, int checkCount, int offset, int byteCount, byte[] tempBuffer) {
        if (tempBuffer == null) {
            return super.checkSomeShards(matrixRows, inputs, inputCount, toCheck, checkCount, offset, byteCount, null);
        }
        int count = offset + byteCount;
        byte[] inputShard = new byte[count];
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < checkCount; ++iOutput) {
            int iByte;
            byte[] outputShard = toCheck[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            int iInput = 0;
            inputs[0].getBytes(0, inputShard);
            byte[] multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < count; ++iByte) {
                tempBuffer[iByte] = multTableRow[inputShard[iByte] & 0xFF];
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputs[iInput].getBytes(0, inputShard);
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < count; ++iByte) {
                    int n = iByte;
                    tempBuffer[n] = (byte)(tempBuffer[n] ^ multTableRow[inputShard[iByte] & 0xFF]);
                }
            }
            for (int iByte2 = offset; iByte2 < count; ++iByte2) {
                if (tempBuffer[iByte2] == outputShard[iByte2]) continue;
                return false;
            }
        }
        return true;
    }
}

