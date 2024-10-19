package io.jpower.kcp.netty.erasure.fecNative;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.jpower.kcp.netty.erasure.IFecEncode;
import io.jpower.kcp.netty.erasure.fec.Fec;

public class FecEncode implements IFecEncode {
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
   private ReedSolomonNative codec;

   public FecEncode(int headerOffset, ReedSolomonNative codec, int mtu) {
      this.dataShards = codec.getDataShards();
      this.parityShards = codec.getParityShards();
      this.shardSize = this.dataShards + this.parityShards;
      this.paws = 4294967295L / (long)this.shardSize * (long)this.shardSize;
      this.headerOffset = headerOffset;
      this.payloadOffset = headerOffset + Fec.fecHeaderSize;
      this.codec = codec;
      this.shardCache = new ByteBuf[this.shardSize];
      this.encodeCache = new ByteBuf[this.parityShards];
      this.zeros = ByteBufAllocator.DEFAULT.buffer(mtu);
      this.zeros.writeBytes(new byte[mtu]);
   }

   public ByteBuf[] encode(ByteBuf byteBuf) {
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
      } else {
         long[] shards = new long[dataShards + parityShards];

         int i;
         ByteBuf shard;
         for(i = 0; i < parityShards; ++i) {
            shard = ByteBufAllocator.DEFAULT.buffer(this.maxSize);
            shardCache[i + dataShards] = shard;
            encodeCache[i] = shard;
            this.markParity(shard, headerOffset);
            shard.writerIndex(this.maxSize);
            shards[i + dataShards] = shard.memoryAddress() + (long)payloadOffset;
         }

         for(i = 0; i < dataShards; ++i) {
            shard = shardCache[i];
            shards[i] = shard.memoryAddress() + (long)payloadOffset;
            int left = this.maxSize - shard.writerIndex();
            if (left > 0) {
               shard.writeBytes(zeros, left);
               zeros.readerIndex(0);
            }
         }

         this.codec.rsEncode(shards, this.maxSize - payloadOffset);

         for(i = 0; i < dataShards; ++i) {
            shardCache[i].release();
            shardCache[i] = null;
         }

         this.shardCount = 0;
         this.maxSize = 0;
         return encodeCache;
      }
   }

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

      for(int i = 0; i < this.dataShards; ++i) {
         byteBuf = this.shardCache[i];
         if (byteBuf != null) {
            byteBuf.release();
         }
      }

      this.zeros.release();
      this.codec = null;
   }

   public static void main(String[] args) {
      int a = Integer.MAX_VALUE;
      ++a;
      System.out.println(a % Integer.MAX_VALUE);
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
