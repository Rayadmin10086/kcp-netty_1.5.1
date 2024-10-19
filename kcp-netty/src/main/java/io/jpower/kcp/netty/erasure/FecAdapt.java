package io.jpower.kcp.netty.erasure;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.jpower.kcp.netty.erasure.fecNative.FecDecode;
import io.jpower.kcp.netty.erasure.fecNative.FecEncode;
import io.jpower.kcp.netty.erasure.fecNative.ReedSolomonNative;

public class FecAdapt {
   private static final InternalLogger log = InternalLoggerFactory.getInstance(FecAdapt.class);
   private ReedSolomonNative reedSolomonNative;
   private ReedSolomon reedSolomon;

   public FecAdapt(int dataShards, int parityShards) {
      if (ReedSolomonNative.isNativeSupport()) {
         this.reedSolomonNative = new ReedSolomonNative(dataShards, parityShards);
         log.info("fec use C native reedSolomon dataShards {} parityShards {}", dataShards, parityShards);
      } else {
         this.reedSolomon = ReedSolomon.create(dataShards, parityShards);
         log.info("fec use jvm reedSolomon dataShards {} parityShards {}", dataShards, parityShards);
      }
   }

   public IFecEncode fecEncode(int headerOffset, int mtu) {
      IFecEncode iFecEncode;
      if (this.reedSolomonNative != null) {
         iFecEncode = new FecEncode(headerOffset, this.reedSolomonNative, mtu);
      } else {
         iFecEncode = new kcp.highway.erasure.fec.FecEncode(headerOffset, this.reedSolomon, mtu);
      }

      return iFecEncode;
   }

   public IFecDecode fecDecode(int mtu) {
      IFecDecode iFecDecode;
      if (this.reedSolomonNative != null) {
         iFecDecode = new FecDecode(3 * this.reedSolomonNative.getTotalShardCount(), this.reedSolomonNative, mtu);
      } else {
         iFecDecode = new kcp.highway.erasure.fec.FecDecode(3 * this.reedSolomon.getTotalShardCount(), this.reedSolomon, mtu);
      }

      return iFecDecode;
   }
}
