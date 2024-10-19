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

public class SampleEncoder {
    public static final int DATA_SHARDS = 4;
    public static final int PARITY_SHARDS = 2;
    public static final int TOTAL_SHARDS = 6;
    public static final int BYTES_IN_INT = 4;

    public static void main(String[] arguments) throws IOException {
        if (arguments.length != 1) {
            System.out.println("Usage: SampleEncoder <fileName>");
            return;
        }
        File inputFile = new File(arguments[0]);
        if (!inputFile.exists()) {
            System.out.println("Cannot read input file: " + inputFile);
            return;
        }
        int fileSize = (int)inputFile.length();
        int storedSize = fileSize + 4;
        int shardSize = (storedSize + 4 - 1) / 4;
        int bufferSize = shardSize * 4;
        byte[] allBytes = new byte[bufferSize];
        ByteBuffer.wrap(allBytes).putInt(fileSize);
        FileInputStream in = new FileInputStream(inputFile);
        int bytesRead = ((InputStream)in).read(allBytes, 4, fileSize);
        if (bytesRead != fileSize) {
            throw new IOException("not enough bytes read");
        }
        ((InputStream)in).close();
        byte[][] shards = new byte[6][shardSize];
        for (int i = 0; i < 4; ++i) {
            System.arraycopy(allBytes, i * shardSize, shards[i], 0, shardSize);
        }
        ReedSolomon reedSolomon = ReedSolomon.create(4, 2);
        reedSolomon.encodeParity(shards, 0, shardSize);
        for (int i = 0; i < 6; ++i) {
            File outputFile = new File(inputFile.getParentFile(), inputFile.getName() + "." + i);
            FileOutputStream out = new FileOutputStream(outputFile);
            ((OutputStream)out).write(shards[i]);
            ((OutputStream)out).close();
            System.out.println("wrote " + outputFile);
        }
    }
}

