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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kcp.highway.erasure.IFecDecode;
import kcp.highway.erasure.ReedSolomon;
import kcp.highway.erasure.fec.Fec;
import kcp.highway.erasure.fec.FecException;
import kcp.highway.erasure.fec.FecPacket;
import kcp.highway.erasure.fec.MyArrayList;
import kcp.highway.erasure.fec.Snmp;

public class FecDecode
implements IFecDecode {
    private int rxlimit;
    private int dataShards;
    private int parityShards;
    private int shardSize;
    private MyArrayList<FecPacket> rx;
    private ByteBuf[] decodeCache;
    private boolean[] flagCache;
    private ByteBuf zeros;
    private ReedSolomon codec;

    public FecDecode(int rxlimit, ReedSolomon codec, int mtu) {
        this.rxlimit = rxlimit;
        this.dataShards = codec.getDataShardCount();
        this.parityShards = codec.getParityShardCount();
        this.shardSize = this.dataShards + this.parityShards;
        if (this.dataShards <= 0 || this.parityShards <= 0) {
            throw new FecException("dataShards and parityShards can not less than 0");
        }
        if (rxlimit < this.dataShards + this.parityShards) {
            throw new FecException("");
        }
        this.codec = codec;
        this.decodeCache = new ByteBuf[this.shardSize];
        this.flagCache = new boolean[this.shardSize];
        this.rx = new MyArrayList(rxlimit);
        this.zeros = ByteBufAllocator.DEFAULT.buffer(mtu);
        this.zeros.writeBytes(new byte[mtu]);
    }

    @Override
    public List<ByteBuf> decode(FecPacket pkt) {
        int searchEnd;
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
        for (int i = n; i >= 0; --i) {
            if (pkt.getSeqid() == ((FecPacket)rx.get(i)).getSeqid()) {
                Snmp.snmp.FECRepeatDataShards.increment();
                pkt.release();
                return null;
            }
            if (pkt.getSeqid() <= ((FecPacket)rx.get(i)).getSeqid()) continue;
            insertIdx = i + 1;
            break;
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
        if ((searchEnd = searchBegin + shardSize - 1) >= rx.size()) {
            searchEnd = rx.size() - 1;
        }
        ArrayList<ByteBuf> result = null;
        if (searchEnd - searchBegin + 1 >= dataShards) {
            FecPacket fecPacket;
            long seqid;
            int i;
            int numshard = 0;
            int numDataShard = 0;
            int first = 0;
            int maxlen = 0;
            ByteBuf[] shards = this.decodeCache;
            boolean[] shardsflag = this.flagCache;
            for (i = 0; i < shards.length; ++i) {
                shards[i] = null;
                shardsflag[i] = false;
            }
            for (i = searchBegin; i <= searchEnd && (seqid = (fecPacket = (FecPacket)rx.get(i)).getSeqid()) <= shardEnd; ++i) {
                if (seqid < shardBegin) continue;
                shards[(int)(seqid % (long)shardSize)] = fecPacket.getData();
                shardsflag[(int)(seqid % (long)shardSize)] = true;
                ++numshard;
                if (fecPacket.getFlag() == typeData) {
                    ++numDataShard;
                }
                if (numshard == 1) {
                    first = i;
                }
                if (fecPacket.getData().readableBytes() <= maxlen) continue;
                maxlen = fecPacket.getData().readableBytes();
            }
            if (numDataShard == dataShards) {
                FecDecode.freeRange(first, numshard, rx);
            } else if (numshard >= dataShards) {
                for (i = 0; i < shards.length; ++i) {
                    ByteBuf shard = shards[i];
                    if (shard == null) {
                        shards[i] = zeros.copy(0, maxlen);
                        shards[i].writerIndex(maxlen);
                        continue;
                    }
                    int left = maxlen - shard.readableBytes();
                    if (left <= 0) continue;
                    shard.writeBytes(zeros, left);
                    zeros.resetReaderIndex();
                }
                this.codec.decodeMissing(shards, shardsflag, 0, maxlen);
                result = new ArrayList<ByteBuf>(dataShards);
                for (i = 0; i < shardSize; ++i) {
                    if (shardsflag[i]) continue;
                    ByteBuf byteBufs = shards[i];
                    if (i >= dataShards) {
                        byteBufs.release();
                        continue;
                    }
                    short packageSize = byteBufs.readShort();
                    if (byteBufs.readableBytes() < packageSize) {
                        System.out.println("bytebuf\u00e9\u2022\u00bf\u00e5\u00ba\u00a6: " + byteBufs.writerIndex() + " \u00e8\u00af\u00bb\u00e5\u2021\u00ba\u00e9\u2022\u00bf\u00e5\u00ba\u00a6" + packageSize);
                        byte[] bytes = new byte[byteBufs.writerIndex()];
                        byteBufs.getBytes(0, bytes);
                        for (byte aByte : bytes) {
                            System.out.print("[" + aByte + "] ");
                        }
                        Snmp.snmp.FECErrs.increment();
                    } else {
                        Snmp.snmp.FECRecovered.increment();
                    }
                    byteBufs = byteBufs.slice(Fec.fecDataSize, (int)packageSize);
                    result.add(byteBufs);
                    Snmp.snmp.FECRecovered.increment();
                }
                FecDecode.freeRange(first, numshard, rx);
            }
        }
        if (rx.size() > this.rxlimit) {
            if (((FecPacket)rx.get(0)).getFlag() == Fec.typeData) {
                Snmp.snmp.FECShortShards.increment();
            }
            FecDecode.freeRange(0, 1, rx);
        }
        return result;
    }

    @Override
    public void release() {
        this.rxlimit = 0;
        this.dataShards = 0;
        this.parityShards = 0;
        this.shardSize = 0;
        for (FecPacket fecPacket : this.rx) {
            if (fecPacket == null) continue;
            fecPacket.release();
        }
        this.zeros.release();
        this.codec = null;
    }

    private static void freeRange(int first, int n, MyArrayList<FecPacket> q) {
        int toIndex = first + n;
        for (int i = first; i < toIndex; ++i) {
            ((FecPacket)q.get(i)).release();
        }
        q.removeRange(first, toIndex);
    }

    /*
     * Unable to fully structure code
     */
    public static void main(String[] args) {
        block0: while (true) {
            if ((size = new Random().nextInt(99) + 1) <= (first = new Random().nextInt(100))) {
                continue;
            }
            n = new Random().nextInt(size - first);
            q = FecDecode.build(size);
            FecDecode.remove(first, n, q);
            newQ = FecDecode.build(size);
            newQ.removeRange(first, first + n);
            if (newQ.size() != q.size()) {
                System.out.println();
            }
            i = 0;
            while (true) {
                if (i < newQ.size()) {
                    break;  // 退出循环
                    continue block0;
                }
                if (newQ.get(i) != q.get(i)) {
                    System.out.println();
                }
                ++i;
            }
            break;
        }
    }

    public static MyArrayList<Integer> build(int size) {
        MyArrayList<Integer> q = new MyArrayList<Integer>(size);
        for (int i = 0; i < size; ++i) {
            q.add(i);
        }
        return q;
    }

    private static void remove(int first, int n, MyArrayList<Integer> q) {
        int index;
        int i;
        for (i = first; i < q.size() && (index = i + n) != q.size(); ++i) {
            q.set(i, (Integer)q.get(index));
        }
        for (i = 0; i < n; ++i) {
            q.remove(q.size() - 1);
        }
    }
}

