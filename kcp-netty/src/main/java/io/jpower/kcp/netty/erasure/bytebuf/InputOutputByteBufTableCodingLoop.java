/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package kcp.highway.erasure.bytebuf;

import io.netty.buffer.ByteBuf;
import kcp.highway.erasure.Galois;
import kcp.highway.erasure.bytebuf.ByteBufCodingLoopBase;

public class InputOutputByteBufTableCodingLoop
extends ByteBufCodingLoopBase {
    @Override
    public void codeSomeShards(byte[][] matrixRows, ByteBuf[] inputs, int inputCount, ByteBuf[] outputs, int outputCount, int offset, int byteCount) {
        int iByte;
        byte[] multTableRow;
        byte[] matrixRow;
        ByteBuf outputShard;
        int iOutput;
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        int iInput = 0;
        ByteBuf inputShard = inputs[0];
        for (iOutput = 0; iOutput < outputCount; ++iOutput) {
            outputShard = outputs[iOutput];
            matrixRow = matrixRows[iOutput];
            multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                outputShard.setByte(iByte, (int)multTableRow[inputShard.getByte(iByte) & 0xFF]);
            }
        }
        for (iInput = 1; iInput < inputCount; ++iInput) {
            inputShard = inputs[iInput];
            for (iOutput = 0; iOutput < outputCount; ++iOutput) {
                outputShard = outputs[iOutput];
                matrixRow = matrixRows[iOutput];
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    byte temp = outputShard.getByte(iByte);
                    temp = (byte)(temp ^ multTableRow[inputShard.getByte(iByte) & 0xFF]);
                    outputShard.setByte(iByte, (int)temp);
                }
            }
        }
    }

    @Override
    public boolean checkSomeShards(byte[][] matrixRows, ByteBuf[] inputs, int inputCount, byte[][] toCheck, int checkCount, int offset, int byteCount, byte[] tempBuffer) {
        if (tempBuffer == null) {
            return super.checkSomeShards(matrixRows, inputs, inputCount, toCheck, checkCount, offset, byteCount, null);
        }
        byte[][] table = Galois.MULTIPLICATION_TABLE;
        for (int iOutput = 0; iOutput < checkCount; ++iOutput) {
            int iByte;
            byte[] outputShard = toCheck[iOutput];
            byte[] matrixRow = matrixRows[iOutput];
            int iInput = 0;
            ByteBuf inputShard = inputs[0];
            byte[] multTableRow = table[matrixRow[0] & 0xFF];
            for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                tempBuffer[iByte] = multTableRow[inputShard.getByte(iByte) & 0xFF];
            }
            for (iInput = 1; iInput < inputCount; ++iInput) {
                inputShard = inputs[iInput];
                multTableRow = table[matrixRow[iInput] & 0xFF];
                for (iByte = offset; iByte < offset + byteCount; ++iByte) {
                    int n = iByte;
                    tempBuffer[n] = (byte)(tempBuffer[n] ^ multTableRow[inputShard.getByte(iByte) & 0xFF]);
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

