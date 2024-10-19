/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure.fecNative;

public class ReedSolomonC {
    private static boolean nativeSupport = true;

    public static boolean isNativeSupport() {
        return nativeSupport;
    }

    protected static native void init();

    protected native long rsNew(int var1, int var2);

    protected native void rsRelease(long var1);

    protected native void rsEncode(long var1, long[] var3, int var4);

    protected native void rsReconstruct(long var1, long[] var3, boolean[] var4, int var5);

    static {
        try {
            String path = System.getProperty("user.dir");
            System.load(path + "/kcp-fec/src/main/java/com/backblaze/erasure/fecNative/native/libjni.dylib");
            ReedSolomonC.init();
        }
        catch (Throwable e) {
            nativeSupport = false;
        }
    }
}

