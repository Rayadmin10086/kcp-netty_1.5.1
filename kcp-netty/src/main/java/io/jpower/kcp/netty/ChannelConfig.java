package io.jpower.kcp.netty;

import io.jpower.kcp.netty.erasure.FecAdapt;
import io.jpower.kcp.netty.threadPool.IMessageExecutorPool;
import io.jpower.kcp.netty.netty.NettyMessageExecutorPool;

public class ChannelConfig {
   public static final int crc32Size = 4;
   private long conv;
   private boolean nodelay;
   private int interval = 100;
   private int fastresend;
   private boolean nocwnd;
   private int sndwnd = 256;
   private int rcvwnd = 256;
   private int mtu = 1400;
   private long timeoutMillis;
   private boolean stream;
   private FecAdapt fecAdapt;
   private boolean ackNoDelay = false;
   private boolean fastFlush = true;
   private boolean crc32Check = false;
   private int readBufferSize = -1;
   private int writeBufferSize = -1;
   private int ackMaskSize = 0;
   private boolean useConvChannel = false;
   private IMessageExecutorPool iMessageExecutorPool = new NettyMessageExecutorPool(Runtime.getRuntime().availableProcessors());

   public void nodelay(boolean nodelay, int interval, int resend, boolean nc) {
      this.nodelay = nodelay;
      this.interval = interval;
      this.fastresend = resend;
      this.nocwnd = nc;
   }

   public int getReadBufferSize() {
      return this.readBufferSize;
   }

   public void setReadBufferSize(int readBufferSize) {
      this.readBufferSize = readBufferSize;
   }

   public IMessageExecutorPool getiMessageExecutorPool() {
      return this.iMessageExecutorPool;
   }

   public void setiMessageExecutorPool(IMessageExecutorPool iMessageExecutorPool) {
      if (this.iMessageExecutorPool != null) {
         this.iMessageExecutorPool.stop();
      }

      this.iMessageExecutorPool = iMessageExecutorPool;
   }

   public boolean isNodelay() {
      return this.nodelay;
   }

   public long getConv() {
      return this.conv;
   }

   public void setConv(long conv) {
      this.conv = conv;
   }

   public int getInterval() {
      return this.interval;
   }

   public int getFastresend() {
      return this.fastresend;
   }

   public boolean isNocwnd() {
      return this.nocwnd;
   }

   public int getSndwnd() {
      return this.sndwnd;
   }

   public void setSndwnd(int sndwnd) {
      this.sndwnd = sndwnd;
   }

   public int getRcvwnd() {
      return this.rcvwnd;
   }

   public void setRcvwnd(int rcvwnd) {
      this.rcvwnd = rcvwnd;
   }

   public int getMtu() {
      return this.mtu;
   }

   public void setMtu(int mtu) {
      this.mtu = mtu;
   }

   public long getTimeoutMillis() {
      return this.timeoutMillis;
   }

   public void setTimeoutMillis(long timeoutMillis) {
      this.timeoutMillis = timeoutMillis;
   }

   public boolean isStream() {
      return this.stream;
   }

   public void setStream(boolean stream) {
      this.stream = stream;
   }

   public FecAdapt getFecAdapt() {
      return this.fecAdapt;
   }

   public void setFecAdapt(FecAdapt fecAdapt) {
      this.fecAdapt = fecAdapt;
   }

   public boolean isAckNoDelay() {
      return this.ackNoDelay;
   }

   public void setAckNoDelay(boolean ackNoDelay) {
      this.ackNoDelay = ackNoDelay;
   }

   public boolean isFastFlush() {
      return this.fastFlush;
   }

   public void setFastFlush(boolean fastFlush) {
      this.fastFlush = fastFlush;
   }

   public boolean isCrc32Check() {
      return this.crc32Check;
   }

   public int getAckMaskSize() {
      return this.ackMaskSize;
   }

   public void setAckMaskSize(int ackMaskSize) {
      this.ackMaskSize = ackMaskSize;
   }

   public void setCrc32Check(boolean crc32Check) {
      this.crc32Check = crc32Check;
   }

   public boolean isUseConvChannel() {
      return this.useConvChannel;
   }

   public int getWriteBufferSize() {
      return this.writeBufferSize;
   }

   public void setWriteBufferSize(int writeBufferSize) {
      this.writeBufferSize = writeBufferSize;
   }

   public void setUseConvChannel(boolean useConvChannel) {
      this.useConvChannel = useConvChannel;
   }
}
