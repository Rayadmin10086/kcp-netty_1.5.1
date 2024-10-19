/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.jpower.kcp.netty.erasure;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.jpower.kcp.netty.erasure.IFecDecode;
import io.jpower.kcp.netty.erasure.IFecEncode;
import io.jpower.kcp.netty.erasure.ReedSolomon;
import io.jpower.kcp.netty.erasure.fec.FecDecode;
import io.jpower.kcp.netty.erasure.fec.FecEncode;
import io.jpower.kcp.netty.erasure.fecNative.ReedSolomonNative;

public class FecAdapt {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(FecAdapt.class);
    private ReedSolomonNative reedSolomonNative;
    private ReedSolomon reedSolomon;

    public FecAdapt(int dataShards, int parityShards) {
        if (ReedSolomonNative.isNativeSupport()) {
            this.reedSolomonNative = new ReedSolomonNative(dataShards, parityShards);
            log.info("fec use C native reedSolomon dataShards {} parityShards {}", (Object)dataShards, (Object)parityShards);
        } else {
            this.reedSolomon = ReedSolomon.create(dataShards, parityShards);
            log.info("fec use jvm reedSolomon dataShards {} parityShards {}", (Object)dataShards, (Object)parityShards);
        }
    }

    public IFecEncode fecEncode(int headerOffset, int mtu) {
        IFecEncode iFecEncode = this.reedSolomonNative != null ? new io.jpower.kcp.netty.erasure.fecNative.FecEncode(headerOffset, this.reedSolomonNative, mtu) : new FecEncode(headerOffset, this.reedSolomon, mtu);
        return iFecEncode;
    }

    public IFecDecode fecDecode(int mtu) {
        IFecDecode iFecDecode = this.reedSolomonNative != null ? new io.jpower.kcp.netty.erasure.fecNative.FecDecode(3 * this.reedSolomonNative.getTotalShardCount(), this.reedSolomonNative, mtu) : new FecDecode(3 * this.reedSolomon.getTotalShardCount(), this.reedSolomon, mtu);
        return iFecDecode;
    }
}

