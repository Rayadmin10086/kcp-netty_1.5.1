/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 */
package kcp.highway.erasure.fec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import kcp.highway.erasure.IFecEncode;
import kcp.highway.erasure.ReedSolomon;
import kcp.highway.erasure.fec.Fec;

public class FecEncode
implements IFecEncode {
    private int dataShards;
    private int parityShards;
    private int shardSize;
    private long paws;
    private long next;
    private int shardCount;
    private int maxSize;
    private int headerOffset;
    private int payloadOffset;
    private ByteBuf[] shardCache;
    private ByteBuf[] encodeCache;
    private ByteBuf zeros;
    private ReedSolomon codec;

    public FecEncode(int headerOffset, ReedSolomon codec, int mtu) {
        this.dataShards = codec.getDataShardCount();
        this.parityShards = codec.getParityShardCount();
        this.shardSize = this.dataShards + this.parityShards;
        this.paws = 0xFFFFFFFFL / (long)this.shardSize * (long)this.shardSize;
        this.headerOffset = headerOffset;
        this.payloadOffset = headerOffset + Fec.fecHeaderSize;
        this.codec = codec;
        this.shardCache = new ByteBuf[this.shardSize];
        this.encodeCache = new ByteBuf[this.parityShards];
        this.zeros = ByteBufAllocator.DEFAULT.buffer(mtu);
        this.zeros.writeBytes(new byte[mtu]);
    }

    @Override
    public ByteBuf[] encode(ByteBuf byteBuf) {
        int i;
        int headerOffset = this.headerOffset;
        int payloadOffset = this.payloadOffset;
        int dataShards = this.dataShards;
        int parityShards = this.parityShards;
        ByteBuf[] shardCache = this.shardCache;
        ByteBuf[] encodeCache = this.encodeCache;
        ByteBuf zeros = this.zeros;
        this.markData(byteBuf, headerOffset);
        int sz = byteBuf.writerIndex();
        byteBuf.setShort(payloadOffset, sz - headerOffset - Fec.fecHeaderSizePlus2);
        shardCache[this.shardCount] = byteBuf.retainedDuplicate();
        ++this.shardCount;
        if (sz > this.maxSize) {
            this.maxSize = sz;
        }
        if (this.shardCount != dataShards) {
            return null;
        }
        for (i = 0; i < parityShards; ++i) {
            ByteBuf parityByte;
            shardCache[i + dataShards] = parityByte = ByteBufAllocator.DEFAULT.buffer(this.maxSize);
            encodeCache[i] = parityByte;
            this.markParity(parityByte, headerOffset);
            parityByte.writerIndex(this.maxSize);
        }
        for (i = 0; i < dataShards; ++i) {
            ByteBuf shard = shardCache[i];
            int left = this.maxSize - shard.writerIndex();
            if (left <= 0) continue;
            shard.writeBytes(zeros, left);
            zeros.readerIndex(0);
        }
        this.codec.encodeParity(shardCache, payloadOffset, this.maxSize - payloadOffset);
        for (i = 0; i < dataShards; ++i) {
            shardCache[i].release();
            shardCache[i] = null;
        }
        this.shardCount = 0;
        this.maxSize = 0;
        return encodeCache;
    }

    @Override
    public void release() {
        this.dataShards = 0;
        this.parityShards = 0;
        this.shardSize = 0;
        this.paws = 0L;
        this.next = 0L;
        this.shardCount = 0;
        this.maxSize = 0;
        this.headerOffset = 0;
        this.payloadOffset = 0;
        ByteBuf byteBuf = null;
        for (int i = 0; i < this.dataShards; ++i) {
            byteBuf = this.shardCache[i];
            if (byteBuf == null) continue;
            byteBuf.release();
        }
        this.zeros.release();
        this.codec = null;
    }

    public static void main(String[] args) {
        int a = Integer.MAX_VALUE;
        System.out.println(++a % Integer.MAX_VALUE);
    }

    private void markData(ByteBuf byteBuf, int offset) {
        byteBuf.setIntLE(offset, (int)this.next);
        byteBuf.setShortLE(offset + 4, Fec.typeData);
        ++this.next;
    }

    private void markParity(ByteBuf byteBuf, int offset) {
        byteBuf.setIntLE(offset, (int)this.next);
        byteBuf.setShortLE(offset + 4, Fec.typeParity);
        this.next = (this.next + 1L) % this.paws;
    }
}

