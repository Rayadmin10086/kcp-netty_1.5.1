package io.jpower.kcp.netty.erasure.fecNative;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import io.jpower.kcp.netty.erasure.IFecDecode;
import io.jpower.kcp.netty.erasure.fec.Fec;
import io.jpower.kcp.netty.erasure.fec.FecException;
import io.jpower.kcp.netty.erasure.fec.FecPacket;
import io.jpower.kcp.netty.erasure.fec.MyArrayList;
import io.jpower.kcp.netty.erasure.fec.Snmp;

public class FecDecode implements IFecDecode {
   private int rxlimit;
   private int dataShards;
   private int parityShards;
   private int shardSize;
   private MyArrayList<FecPacket> rx;
   private ByteBuf[] decodeCache;
   private boolean[] flagCache;
   private ByteBuf zeros;
   private ReedSolomonNative codec;

   public FecDecode(int rxlimit, ReedSolomonNative codec, int mtu) {
      this.rxlimit = rxlimit;
      this.dataShards = codec.getDataShards();
      this.parityShards = codec.getParityShards();
      this.shardSize = this.dataShards + this.parityShards;
      if (this.dataShards > 0 && this.parityShards > 0) {
         if (rxlimit < this.dataShards + this.parityShards) {
            throw new FecException("");
         } else {
            this.codec = codec;
            this.decodeCache = new ByteBuf[this.shardSize];
            this.flagCache = new boolean[this.shardSize];
            this.rx = new MyArrayList(rxlimit);
            this.zeros = ByteBufAllocator.DEFAULT.buffer(mtu);
            this.zeros.writeBytes(new byte[mtu]);
         }
      } else {
         throw new FecException("dataShards and parityShards can not less than 0");
      }
   }

   public List<ByteBuf> decode(FecPacket pkt) {
      int shardSize = this.shardSize;
      MyArrayList<FecPacket> rx = this.rx;
      int dataShards = this.dataShards;
      ByteBuf zeros = this.zeros;
      int typeData = Fec.typeData;
      if (pkt.getFlag() == Fec.typeParity) {
         Snmp.snmp.FECParityShards.increment();
      } else {
         Snmp.snmp.FECDataShards.increment();
      }

      int n = rx.size() - 1;
      int insertIdx = 0;

      for(int i = n; i >= 0; --i) {
         if (pkt.getSeqid() == ((FecPacket)rx.get(i)).getSeqid()) {
            Snmp.snmp.FECRepeatDataShards.increment();
            pkt.release();
            return null;
         }

         if (pkt.getSeqid() > ((FecPacket)rx.get(i)).getSeqid()) {
            insertIdx = i + 1;
            break;
         }
      }

      if (insertIdx == n + 1) {
         rx.add(pkt);
      } else {
         rx.add(insertIdx, pkt);
      }

      long shardBegin = pkt.getSeqid() - pkt.getSeqid() % (long)shardSize;
      long shardEnd = shardBegin + (long)shardSize - 1L;
      int searchBegin = (int)((long)insertIdx - pkt.getSeqid() % (long)shardSize);
      if (searchBegin < 0) {
         searchBegin = 0;
      }

      int searchEnd = searchBegin + shardSize - 1;
      if (searchEnd >= rx.size()) {
         searchEnd = rx.size() - 1;
      }

      List<ByteBuf> result = null;
      if (searchEnd - searchBegin + 1 >= dataShards) {
         int numshard = 0;
         int numDataShard = 0;
         int first = 0;
         int maxlen = 0;
         ByteBuf[] shards = this.decodeCache;
         boolean[] shardsflag = this.flagCache;

         int i;
         for(i = 0; i < shards.length; ++i) {
            shards[i] = null;
            shardsflag[i] = false;
         }

         for(i = searchBegin; i <= searchEnd; ++i) {
            FecPacket fecPacket = (FecPacket)rx.get(i);
            long seqid = fecPacket.getSeqid();
            if (seqid > shardEnd) {
               break;
            }

            if (seqid >= shardBegin) {
               shards[(int)(seqid % (long)shardSize)] = fecPacket.getData();
               shardsflag[(int)(seqid % (long)shardSize)] = true;
               ++numshard;
               if (fecPacket.getFlag() == typeData) {
                  ++numDataShard;
               }

               if (numshard == 1) {
                  first = i;
               }

               if (fecPacket.getData().readableBytes() > maxlen) {
                  maxlen = fecPacket.getData().readableBytes();
               }
            }
         }

         if (numDataShard == dataShards) {
            freeRange(first, numshard, rx);
         } else if (numshard >= dataShards) {
            long[] shardsAddress = new long[shards.length];

            int i;
            ByteBuf byteBufs;
            for(i = 0; i < shards.length; ++i) {
               byteBufs = shards[i];
               if (byteBufs == null) {
                  shards[i] = zeros.copy(0, maxlen);
                  shards[i].writerIndex(maxlen);
               } else {
                  int left = maxlen - byteBufs.readableBytes();
                  if (left > 0) {
                     byteBufs.writeBytes(zeros, left);
                     zeros.resetReaderIndex();
                  }

                  shardsAddress[i] = byteBufs.memoryAddress();
               }
            }

            this.codec.rsReconstruct(shardsAddress, shardsflag, maxlen);
            result = new ArrayList(dataShards);

            for(i = 0; i < shardSize; ++i) {
               if (!shardsflag[i]) {
                  byteBufs = shards[i];
                  if (i >= dataShards) {
                     byteBufs.release();
                  } else {
                     int packageSize = byteBufs.readShort();
                     if (byteBufs.readableBytes() < packageSize) {
                        PrintStream var10000 = System.out;
                        int var10001 = byteBufs.writerIndex();
                        var10000.println("bytebufé•¿åº¦: " + var10001 + " è¯»å‡ºé•¿åº¦" + packageSize);
                        byte[] bytes = new byte[byteBufs.writerIndex()];
                        byteBufs.getBytes(0, bytes);
                        byte[] var27 = bytes;
                        int var28 = bytes.length;

                        for(int var29 = 0; var29 < var28; ++var29) {
                           byte aByte = var27[var29];
                           System.out.print("[" + aByte + "] ");
                        }

                        Snmp.snmp.FECErrs.increment();
                     } else {
                        Snmp.snmp.FECRecovered.increment();
                     }

                     byteBufs = byteBufs.slice(Fec.fecDataSize, packageSize);
                     result.add(byteBufs);
                     Snmp.snmp.FECRecovered.increment();
                  }
               }
            }

            freeRange(first, numshard, rx);
         }
      }

      if (rx.size() > this.rxlimit) {
         if (((FecPacket)rx.get(0)).getFlag() == Fec.typeData) {
            Snmp.snmp.FECShortShards.increment();
         }

         freeRange(0, 1, rx);
      }

      return result;
   }

   public void release() {
      this.rxlimit = 0;
      this.dataShards = 0;
      this.parityShards = 0;
      this.shardSize = 0;
      Iterator var1 = this.rx.iterator();

      while(var1.hasNext()) {
         FecPacket fecPacket = (FecPacket)var1.next();
         if (fecPacket != null) {
            fecPacket.release();
         }
      }

      this.zeros.release();
      this.codec = null;
   }

   private static void freeRange(int first, int n, MyArrayList<FecPacket> q) {
      int toIndex = first + n;

      for(int i = first; i < toIndex; ++i) {
         ((FecPacket)q.get(i)).release();
      }

      q.removeRange(first, toIndex);
   }

   public static void main(String[] args) {
      while(true) {
         int size = (new Random()).nextInt(99) + 1;
         int first = (new Random()).nextInt(100);
         if (size > first) {
            int n = (new Random()).nextInt(size - first);
            MyArrayList<Integer> q = build(size);
            remove(first, n, q);
            MyArrayList<Integer> newQ = build(size);
            newQ.removeRange(first, first + n);
            if (newQ.size() != q.size()) {
               System.out.println();
            }

            for(int i = 0; i < newQ.size(); ++i) {
               if (newQ.get(i) != q.get(i)) {
                  System.out.println();
               }
            }
         }
      }
   }

   public static MyArrayList<Integer> build(int size) {
      MyArrayList<Integer> q = new MyArrayList(size);

      for(int i = 0; i < size; ++i) {
         q.add(i);
      }

      return q;
   }

   private static void remove(int first, int n, MyArrayList<Integer> q) {
      int i;
      for(i = first; i < q.size(); ++i) {
         int index = i + n;
         if (index == q.size()) {
            break;
         }

         q.set(i, (Integer)q.get(index));
      }

      for(i = 0; i < n; ++i) {
         q.remove(q.size() - 1);
      }

   }
}
