/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.util.Recycler
 *  io.netty.util.Recycler$Handle
 */
package kcp.highway.erasure.fec;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;

public class FecPacket {
    private long seqid;
    private int flag;
    private ByteBuf data;
    private Recycler.Handle<FecPacket> recyclerHandle;
    private static final Recycler<FecPacket> FEC_PACKET_RECYCLER = new Recycler<FecPacket>(){

        protected FecPacket newObject(Recycler.Handle<FecPacket> handle) {
            return new FecPacket(handle);
        }
    };

    public static FecPacket newFecPacket(ByteBuf byteBuf) {
        FecPacket pkt = (FecPacket)FEC_PACKET_RECYCLER.get();
        pkt.seqid = byteBuf.readUnsignedIntLE();
        pkt.flag = byteBuf.readUnsignedShortLE();
        pkt.data = byteBuf.retainedSlice(byteBuf.readerIndex(), byteBuf.capacity() - byteBuf.readerIndex());
        pkt.data.writerIndex(byteBuf.readableBytes());
        return pkt;
    }

    private FecPacket(Recycler.Handle<FecPacket> recyclerHandle) {
        this.recyclerHandle = recyclerHandle;
    }

    public void release() {
        this.seqid = 0L;
        this.flag = 0;
        this.data.release();
        this.data = null;
        this.recyclerHandle.recycle((Object)this);
    }

    public long getSeqid() {
        return this.seqid;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ByteBuf getData() {
        return this.data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    public String toString() {
        return "FecPacket{seqid=" + this.seqid + ", flag=" + this.flag + "}";
    }
}

