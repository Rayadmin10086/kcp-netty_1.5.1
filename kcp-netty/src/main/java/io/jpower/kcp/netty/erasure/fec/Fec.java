/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure.fec;

public class Fec {
    public static int fecHeaderSize = 6;
    public static int fecDataSize = 2;
    public static int fecHeaderSizePlus2 = fecHeaderSize + fecDataSize;
    public static int typeData = 241;
    public static int typeParity = 242;
}

