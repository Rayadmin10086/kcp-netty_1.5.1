/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import io.jpower.kcp.netty.erasure.CodingLoop;
import io.jpower.kcp.netty.erasure.ReedSolomon;

public class ReedSolomonBenchmark {
    private static final int DATA_COUNT = 17;
    private static final int PARITY_COUNT = 3;
    private static final int TOTAL_COUNT = 20;
    private static final int BUFFER_SIZE = 200000;
    private static final int PROCESSOR_CACHE_SIZE = 0xA00000;
    private static final int TWICE_PROCESSOR_CACHE_SIZE = 0x1400000;
    private static final int NUMBER_OF_BUFFER_SETS = 7;
    private static final long MEASUREMENT_DURATION = 2000L;
    private static final Random RANDOM = new Random();
    private int nextBuffer = 0;

    public static void main(String[] args) {
        new ReedSolomonBenchmark().run();
    }

    public void run() {
        System.out.println("preparing...");
        BufferSet[] bufferSets = new BufferSet[7];
        for (int iBufferSet = 0; iBufferSet < 7; ++iBufferSet) {
            bufferSets[iBufferSet] = new BufferSet();
        }
        byte[] tempBuffer = new byte[200000];
        ArrayList<String> summaryLines = new ArrayList<String>();
        StringBuilder csv = new StringBuilder();
        csv.append("Outer,Middle,Inner,Multiply,Encode,Check\n");
        for (CodingLoop codingLoop : CodingLoop.ALL_CODING_LOOPS) {
            Measurement encodeAverage = new Measurement();
            String testName = codingLoop.getClass().getSimpleName() + " encodeParity";
            System.out.println("\nTEST: " + testName);
            ReedSolomon codec = new ReedSolomon(17, 3, codingLoop);
            System.out.println("    warm up...");
            this.doOneEncodeMeasurement(codec, bufferSets);
            this.doOneEncodeMeasurement(codec, bufferSets);
            System.out.println("    testing...");
            for (int iMeasurement = 0; iMeasurement < 10; ++iMeasurement) {
                encodeAverage.add(this.doOneEncodeMeasurement(codec, bufferSets));
            }
            System.out.println(String.format("\nAVERAGE: %s", encodeAverage));
            summaryLines.add(String.format("    %-45s %s", testName, encodeAverage));
            Measurement checkAverage = new Measurement();
            String testName2 = codingLoop.getClass().getSimpleName() + " isParityCorrect";
            System.out.println("\nTEST: " + testName2);
            ReedSolomon codec2 = new ReedSolomon(17, 3, codingLoop);
            System.out.println("    warm up...");
            this.doOneEncodeMeasurement(codec2, bufferSets);
            this.doOneEncodeMeasurement(codec2, bufferSets);
            System.out.println("    testing...");
            for (int iMeasurement = 0; iMeasurement < 10; ++iMeasurement) {
                checkAverage.add(this.doOneCheckMeasurement(codec2, bufferSets, tempBuffer));
            }
            System.out.println(String.format("\nAVERAGE: %s", checkAverage));
            summaryLines.add(String.format("    %-45s %s", testName2, checkAverage));
            csv.append(ReedSolomonBenchmark.codingLoopNameToCsvPrefix(codingLoop.getClass().getSimpleName()));
            csv.append(encodeAverage.getRate());
            csv.append(",");
            csv.append(checkAverage.getRate());
            csv.append("\n");
        }
        System.out.println("\n");
        System.out.println(csv.toString());
        System.out.println("\nSummary:\n");
        for (String line : summaryLines) {
            System.out.println(line);
        }
    }

    private Measurement doOneEncodeMeasurement(ReedSolomon codec, BufferSet[] bufferSets) {
        long passesCompleted = 0L;
        long bytesEncoded = 0L;
        long encodingTime = 0L;
        while (encodingTime < 2000L) {
            BufferSet bufferSet = bufferSets[this.nextBuffer];
            this.nextBuffer = (this.nextBuffer + 1) % bufferSets.length;
            byte[][] shards = bufferSet.buffers;
            long startTime = System.currentTimeMillis();
            codec.encodeParity(shards, 0, 200000);
            long endTime = System.currentTimeMillis();
            encodingTime += endTime - startTime;
            bytesEncoded += 3400000L;
            ++passesCompleted;
        }
        double seconds = (double)encodingTime / 1000.0;
        double megabytes = (double)bytesEncoded / 1000000.0;
        Measurement result = new Measurement(megabytes, seconds);
        System.out.println(String.format("        %s passes, %s", passesCompleted, result));
        return result;
    }

    private Measurement doOneCheckMeasurement(ReedSolomon codec, BufferSet[] bufferSets, byte[] tempBuffer) {
        long passesCompleted = 0L;
        long bytesChecked = 0L;
        long checkingTime = 0L;
        while (checkingTime < 2000L) {
            BufferSet bufferSet = bufferSets[this.nextBuffer];
            this.nextBuffer = (this.nextBuffer + 1) % bufferSets.length;
            byte[][] shards = bufferSet.buffers;
            long startTime = System.currentTimeMillis();
            if (!codec.isParityCorrect(shards, 0, 200000, tempBuffer)) {
                throw new RuntimeException("parity not correct");
            }
            long endTime = System.currentTimeMillis();
            checkingTime += endTime - startTime;
            bytesChecked += 3400000L;
            ++passesCompleted;
        }
        double seconds = (double)checkingTime / 1000.0;
        double megabytes = (double)bytesChecked / 1000000.0;
        Measurement result = new Measurement(megabytes, seconds);
        System.out.println(String.format("        %s passes, %s", passesCompleted, result));
        return result;
    }

    private static String codingLoopNameToCsvPrefix(String className) {
        List<String> names = ReedSolomonBenchmark.splitCamelCase(className);
        return names.get(0) + "," + names.get(1) + "," + names.get(2) + "," + names.get(3) + ",";
    }

    private static List<String> splitCamelCase(String className) {
        String remaining = className;
        ArrayList<String> result = new ArrayList<String>();
        while (!remaining.isEmpty()) {
            boolean found = false;
            for (int i = 1; i < remaining.length(); ++i) {
                if (!Character.isUpperCase(remaining.charAt(i))) continue;
                result.add(remaining.substring(0, i));
                remaining = remaining.substring(i);
                found = true;
                break;
            }
            if (found) continue;
            result.add(remaining);
            remaining = "";
        }
        return result;
    }

    private static class BufferSet {
        public byte[][] buffers = new byte[20][200000];
        public byte[] bigBuffer;

        public BufferSet() {
            for (int iBuffer = 0; iBuffer < 20; ++iBuffer) {
                byte[] buffer = this.buffers[iBuffer];
                for (int iByte = 0; iByte < 200000; ++iByte) {
                    buffer[iByte] = (byte)RANDOM.nextInt(256);
                }
            }
            this.bigBuffer = new byte[4000000];
            for (int i = 0; i < 4000000; ++i) {
                this.bigBuffer[i] = (byte)RANDOM.nextInt(256);
            }
        }
    }

    private static class Measurement {
        private double megabytes;
        private double seconds;

        public Measurement() {
            this.megabytes = 0.0;
            this.seconds = 0.0;
        }

        public Measurement(double megabytes, double seconds) {
            this.megabytes = megabytes;
            this.seconds = seconds;
        }

        public void add(Measurement other) {
            this.megabytes += other.megabytes;
            this.seconds += other.seconds;
        }

        public double getRate() {
            return this.megabytes / this.seconds;
        }

        public String toString() {
            return String.format("%5.1f MB/s", this.getRate());
        }
    }
}

