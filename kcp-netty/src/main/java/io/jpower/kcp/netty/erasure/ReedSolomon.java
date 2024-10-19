/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package io.jpower.kcp.netty.erasure;

import io.netty.buffer.ByteBuf;
import io.jpower.kcp.netty.erasure.CodingLoop;
import io.jpower.kcp.netty.erasure.Galois;
import io.jpower.kcp.netty.erasure.InputOutputByteTableCodingLoop;
import io.jpower.kcp.netty.erasure.Matrix;
import io.jpower.kcp.netty.erasure.bytebuf.ByteBufCodingLoop;
import io.jpower.kcp.netty.erasure.bytebuf.InputOutputByteBufHeapTableCodingLoop;

public class ReedSolomon {
    private final int dataShardCount;
    private final int parityShardCount;
    private final int totalShardCount;
    private final Matrix matrix;
    private final CodingLoop codingLoop;
    private final byte[][] parityRows;
    private static final ByteBufCodingLoop LOOP = new InputOutputByteBufHeapTableCodingLoop();

    public static ReedSolomon create(int dataShardCount, int parityShardCount) {
        return new ReedSolomon(dataShardCount, parityShardCount, new InputOutputByteTableCodingLoop());
    }

    public ReedSolomon(int dataShardCount, int parityShardCount, CodingLoop codingLoop) {
        if (256 < dataShardCount + parityShardCount) {
            throw new IllegalArgumentException("too many shards - max is 256");
        }
        this.dataShardCount = dataShardCount;
        this.parityShardCount = parityShardCount;
        this.codingLoop = codingLoop;
        this.totalShardCount = dataShardCount + parityShardCount;
        this.matrix = ReedSolomon.buildMatrix(dataShardCount, this.totalShardCount);
        this.parityRows = new byte[parityShardCount][];
        for (int i = 0; i < parityShardCount; ++i) {
            this.parityRows[i] = this.matrix.getRow(dataShardCount + i);
        }
    }

    public int getDataShardCount() {
        return this.dataShardCount;
    }

    public int getParityShardCount() {
        return this.parityShardCount;
    }

    public int getTotalShardCount() {
        return this.totalShardCount;
    }

    public void encodeParity(byte[][] shards, int offset, int byteCount) {
        this.checkBuffersAndSizes(shards, offset, byteCount);
        byte[][] outputs = new byte[this.parityShardCount][];
        System.arraycopy(shards, this.dataShardCount, outputs, 0, this.parityShardCount);
        this.codingLoop.codeSomeShards(this.parityRows, shards, this.dataShardCount, outputs, this.parityShardCount, offset, byteCount);
    }

    public void encodeParity(ByteBuf[] shards, int offset, int byteCount) {
        this.checkBuffersAndSizes(shards, offset, byteCount);
        ByteBuf[] outputs = new ByteBuf[this.parityShardCount];
        System.arraycopy(shards, this.dataShardCount, outputs, 0, this.parityShardCount);
        LOOP.codeSomeShards(this.parityRows, shards, this.dataShardCount, outputs, this.parityShardCount, offset, byteCount);
    }

    public boolean isParityCorrect(byte[][] shards, int firstByte, int byteCount) {
        this.checkBuffersAndSizes(shards, firstByte, byteCount);
        byte[][] toCheck = new byte[this.parityShardCount][];
        System.arraycopy(shards, this.dataShardCount, toCheck, 0, this.parityShardCount);
        return this.codingLoop.checkSomeShards(this.parityRows, shards, this.dataShardCount, toCheck, this.parityShardCount, firstByte, byteCount, null);
    }

    public boolean isParityCorrect(byte[][] shards, int firstByte, int byteCount, byte[] tempBuffer) {
        this.checkBuffersAndSizes(shards, firstByte, byteCount);
        if (tempBuffer.length < firstByte + byteCount) {
            throw new IllegalArgumentException("tempBuffer is not big enough");
        }
        byte[][] toCheck = new byte[this.parityShardCount][];
        System.arraycopy(shards, this.dataShardCount, toCheck, 0, this.parityShardCount);
        return this.codingLoop.checkSomeShards(this.parityRows, shards, this.dataShardCount, toCheck, this.parityShardCount, firstByte, byteCount, tempBuffer);
    }

    public void decodeMissing(byte[][] shards, boolean[] shardPresent, int offset, int byteCount) {
        int iShard;
        this.checkBuffersAndSizes(shards, offset, byteCount);
        int numberPresent = 0;
        for (int i = 0; i < this.totalShardCount; ++i) {
            if (!shardPresent[i]) continue;
            ++numberPresent;
        }
        if (numberPresent == this.totalShardCount) {
            return;
        }
        if (numberPresent < this.dataShardCount) {
            throw new IllegalArgumentException("Not enough shards present");
        }
        Matrix subMatrix = new Matrix(this.dataShardCount, this.dataShardCount);
        byte[][] subShards = new byte[this.dataShardCount][];
        int subMatrixRow = 0;
        for (int matrixRow = 0; matrixRow < this.totalShardCount && subMatrixRow < this.dataShardCount; ++matrixRow) {
            if (!shardPresent[matrixRow]) continue;
            for (int c = 0; c < this.dataShardCount; ++c) {
                subMatrix.set(subMatrixRow, c, this.matrix.get(matrixRow, c));
            }
            subShards[subMatrixRow] = shards[matrixRow];
            ++subMatrixRow;
        }
        Matrix dataDecodeMatrix = subMatrix.invert();
        byte[][] outputs = new byte[this.parityShardCount][];
        byte[][] matrixRows = new byte[this.parityShardCount][];
        int outputCount = 0;
        for (iShard = 0; iShard < this.dataShardCount; ++iShard) {
            if (shardPresent[iShard]) continue;
            outputs[outputCount] = shards[iShard];
            matrixRows[outputCount] = dataDecodeMatrix.getRow(iShard);
            ++outputCount;
        }
        this.codingLoop.codeSomeShards(matrixRows, subShards, this.dataShardCount, outputs, outputCount, offset, byteCount);
        outputCount = 0;
        for (iShard = this.dataShardCount; iShard < this.totalShardCount; ++iShard) {
            if (shardPresent[iShard]) continue;
            outputs[outputCount] = shards[iShard];
            matrixRows[outputCount] = this.parityRows[iShard - this.dataShardCount];
            ++outputCount;
        }
        this.codingLoop.codeSomeShards(matrixRows, shards, this.dataShardCount, outputs, outputCount, offset, byteCount);
    }

    public void decodeMissing(ByteBuf[] shards, boolean[] shardPresent, int offset, int byteCount) {
        int iShard;
        this.checkBuffersAndSizes(shards, offset, byteCount);
        int numberPresent = 0;
        for (int i = 0; i < this.totalShardCount; ++i) {
            if (!shardPresent[i]) continue;
            ++numberPresent;
        }
        if (numberPresent == this.totalShardCount) {
            return;
        }
        if (numberPresent < this.dataShardCount) {
            throw new IllegalArgumentException("Not enough shards present");
        }
        Matrix subMatrix = new Matrix(this.dataShardCount, this.dataShardCount);
        ByteBuf[] subShards = new ByteBuf[this.dataShardCount];
        int subMatrixRow = 0;
        for (int matrixRow = 0; matrixRow < this.totalShardCount && subMatrixRow < this.dataShardCount; ++matrixRow) {
            if (!shardPresent[matrixRow]) continue;
            for (int c = 0; c < this.dataShardCount; ++c) {
                subMatrix.set(subMatrixRow, c, this.matrix.get(matrixRow, c));
            }
            subShards[subMatrixRow] = shards[matrixRow];
            ++subMatrixRow;
        }
        Matrix dataDecodeMatrix = subMatrix.invert();
        ByteBuf[] outputs = new ByteBuf[this.parityShardCount];
        byte[][] matrixRows = new byte[this.parityShardCount][];
        int outputCount = 0;
        for (iShard = 0; iShard < this.dataShardCount; ++iShard) {
            if (shardPresent[iShard]) continue;
            outputs[outputCount] = shards[iShard];
            matrixRows[outputCount] = dataDecodeMatrix.getRow(iShard);
            ++outputCount;
        }
        LOOP.codeSomeShards(matrixRows, subShards, this.dataShardCount, outputs, outputCount, offset, byteCount);
        outputCount = 0;
        for (iShard = this.dataShardCount; iShard < this.totalShardCount; ++iShard) {
            if (shardPresent[iShard]) continue;
            outputs[outputCount] = shards[iShard];
            matrixRows[outputCount] = this.parityRows[iShard - this.dataShardCount];
            ++outputCount;
        }
        LOOP.codeSomeShards(matrixRows, shards, this.dataShardCount, outputs, outputCount, offset, byteCount);
    }

    private void checkBuffersAndSizes(ByteBuf[] shards, int offset, int byteCount) {
        if (shards.length != this.totalShardCount) {
            throw new IllegalArgumentException("wrong number of shards: " + shards.length);
        }
        int shardLength = shards[0].readableBytes();
        for (int i = 1; i < shards.length; ++i) {
            if (shards[i].readableBytes() == shardLength) continue;
            throw new IllegalArgumentException("Shards are different sizes");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset is negative: " + offset);
        }
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount is negative: " + byteCount);
        }
        if (shardLength < offset + byteCount) {
            throw new IllegalArgumentException("buffers to small: " + byteCount + offset);
        }
    }

    private void checkBuffersAndSizes(byte[][] shards, int offset, int byteCount) {
        if (shards.length != this.totalShardCount) {
            throw new IllegalArgumentException("wrong number of shards: " + shards.length);
        }
        int shardLength = 0;
        boolean allShardIsEmpty = true;
        for (int i = 1; i < shards.length; ++i) {
            if (shards[i] == null) continue;
            allShardIsEmpty = false;
            if (shardLength == 0) {
                shardLength = shards[i].length;
                continue;
            }
            if (shards[i].length == shardLength) continue;
            throw new IllegalArgumentException("Shards are different sizes");
        }
        if (allShardIsEmpty) {
            throw new IllegalArgumentException("Shards are empty");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset is negative: " + offset);
        }
        if (byteCount < 0) {
            throw new IllegalArgumentException("byteCount is negative: " + byteCount);
        }
        if (shardLength < offset + byteCount) {
            throw new IllegalArgumentException("buffers to small: " + byteCount + offset);
        }
    }

    private static Matrix buildMatrix(int dataShards, int totalShards) {
        Matrix vandermonde = ReedSolomon.vandermonde(totalShards, dataShards);
        Matrix top = vandermonde.submatrix(0, 0, dataShards, dataShards);
        return vandermonde.times(top.invert());
    }

    private static Matrix vandermonde(int rows, int cols) {
        Matrix result = new Matrix(rows, cols);
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                result.set(r, c, Galois.exp((byte)r, c));
            }
        }
        return result;
    }
}

