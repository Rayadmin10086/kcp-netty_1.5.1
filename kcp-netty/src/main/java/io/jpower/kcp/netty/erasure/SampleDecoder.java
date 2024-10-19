/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import io.jpower.kcp.netty.erasure.ReedSolomon;

public class SampleDecoder {
    public static final int DATA_SHARDS = 4;
    public static final int PARITY_SHARDS = 2;
    public static final int TOTAL_SHARDS = 6;
    public static final int BYTES_IN_INT = 4;

    public static void main(String[] arguments) throws IOException {
        int i;
        if (arguments.length != 1) {
            System.out.println("Usage: SampleDecoder <fileName>");
            return;
        }
        File originalFile = new File(arguments[0]);
        if (!originalFile.exists()) {
            System.out.println("Cannot read input file: " + originalFile);
            return;
        }
        byte[][] shards = new byte[6][];
        boolean[] shardPresent = new boolean[6];
        int shardSize = 0;
        int shardCount = 0;
        for (i = 0; i < 6; ++i) {
            File shardFile = new File(originalFile.getParentFile(), originalFile.getName() + "." + i);
            if (!shardFile.exists()) continue;
            shardSize = (int)shardFile.length();
            shards[i] = new byte[shardSize];
            shardPresent[i] = true;
            ++shardCount;
            FileInputStream in = new FileInputStream(shardFile);
            ((InputStream)in).read(shards[i], 0, shardSize);
            ((InputStream)in).close();
            System.out.println("Read " + shardFile);
        }
        if (shardCount < 4) {
            System.out.println("Not enough shards present");
            return;
        }
        for (i = 0; i < 6; ++i) {
            if (shardPresent[i]) continue;
            shards[i] = new byte[shardSize];
        }
        ReedSolomon reedSolomon = ReedSolomon.create(4, 2);
        reedSolomon.decodeMissing(shards, shardPresent, 0, shardSize);
        byte[] allBytes = new byte[shardSize * 4];
        for (int i2 = 0; i2 < 4; ++i2) {
            System.arraycopy(shards[i2], 0, allBytes, shardSize * i2, shardSize);
        }
        int fileSize = ByteBuffer.wrap(allBytes).getInt();
        File decodedFile = new File(originalFile.getParentFile(), originalFile.getName() + ".decoded");
        FileOutputStream out = new FileOutputStream(decodedFile);
        ((OutputStream)out).write(allBytes, 4, fileSize);
        System.out.println("Wrote " + decodedFile);
    }
}

