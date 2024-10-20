/*
 * Decompiled with CFR 0.152.
 */
package io.jpower.kcp.netty.erasure;

import java.util.ArrayList;

public final class Galois {
    public static final int FIELD_SIZE = 256;
    public static final int GENERATING_POLYNOMIAL = 29;
    public static final short[] LOG_TABLE = new short[]{-1, 0, 1, 25, 2, 50, 26, 198, 3, 223, 51, 238, 27, 104, 199, 75, 4, 100, 224, 14, 52, 141, 239, 129, 28, 193, 105, 248, 200, 8, 76, 113, 5, 138, 101, 47, 225, 36, 15, 33, 53, 147, 142, 218, 240, 18, 130, 69, 29, 181, 194, 125, 106, 39, 249, 185, 201, 154, 9, 120, 77, 228, 114, 166, 6, 191, 139, 98, 102, 221, 48, 253, 226, 152, 37, 179, 16, 145, 34, 136, 54, 208, 148, 206, 143, 150, 219, 189, 241, 210, 19, 92, 131, 56, 70, 64, 30, 66, 182, 163, 195, 72, 126, 110, 107, 58, 40, 84, 250, 133, 186, 61, 202, 94, 155, 159, 10, 21, 121, 43, 78, 212, 229, 172, 115, 243, 167, 87, 7, 112, 192, 247, 140, 128, 99, 13, 103, 74, 222, 237, 49, 197, 254, 24, 227, 165, 153, 119, 38, 184, 180, 124, 17, 68, 146, 217, 35, 32, 137, 46, 55, 63, 209, 91, 149, 188, 207, 205, 144, 135, 151, 178, 220, 252, 190, 97, 242, 86, 211, 171, 20, 42, 93, 158, 132, 60, 57, 83, 71, 109, 65, 162, 31, 45, 67, 216, 183, 123, 164, 118, 196, 23, 73, 236, 127, 12, 111, 246, 108, 161, 59, 82, 41, 157, 85, 170, 251, 96, 134, 177, 187, 204, 62, 90, 203, 89, 95, 176, 156, 169, 160, 81, 11, 245, 22, 235, 122, 117, 44, 215, 79, 174, 213, 233, 230, 231, 173, 232, 116, 214, 244, 234, 168, 80, 88, 175};
    static final byte[] EXP_TABLE = new byte[]{1, 2, 4, 8, 16, 32, 64, -128, 29, 58, 116, -24, -51, -121, 19, 38, 76, -104, 45, 90, -76, 117, -22, -55, -113, 3, 6, 12, 24, 48, 96, -64, -99, 39, 78, -100, 37, 74, -108, 53, 106, -44, -75, 119, -18, -63, -97, 35, 70, -116, 5, 10, 20, 40, 80, -96, 93, -70, 105, -46, -71, 111, -34, -95, 95, -66, 97, -62, -103, 47, 94, -68, 101, -54, -119, 15, 30, 60, 120, -16, -3, -25, -45, -69, 107, -42, -79, 127, -2, -31, -33, -93, 91, -74, 113, -30, -39, -81, 67, -122, 17, 34, 68, -120, 13, 26, 52, 104, -48, -67, 103, -50, -127, 31, 62, 124, -8, -19, -57, -109, 59, 118, -20, -59, -105, 51, 102, -52, -123, 23, 46, 92, -72, 109, -38, -87, 79, -98, 33, 66, -124, 21, 42, 84, -88, 77, -102, 41, 82, -92, 85, -86, 73, -110, 57, 114, -28, -43, -73, 115, -26, -47, -65, 99, -58, -111, 63, 126, -4, -27, -41, -77, 123, -10, -15, -1, -29, -37, -85, 75, -106, 49, 98, -60, -107, 55, 110, -36, -91, 87, -82, 65, -126, 25, 50, 100, -56, -115, 7, 14, 28, 56, 112, -32, -35, -89, 83, -90, 81, -94, 89, -78, 121, -14, -7, -17, -61, -101, 43, 86, -84, 69, -118, 9, 18, 36, 72, -112, 61, 122, -12, -11, -9, -13, -5, -21, -53, -117, 11, 22, 44, 88, -80, 125, -6, -23, -49, -125, 27, 54, 108, -40, -83, 71, -114, 1, 2, 4, 8, 16, 32, 64, -128, 29, 58, 116, -24, -51, -121, 19, 38, 76, -104, 45, 90, -76, 117, -22, -55, -113, 3, 6, 12, 24, 48, 96, -64, -99, 39, 78, -100, 37, 74, -108, 53, 106, -44, -75, 119, -18, -63, -97, 35, 70, -116, 5, 10, 20, 40, 80, -96, 93, -70, 105, -46, -71, 111, -34, -95, 95, -66, 97, -62, -103, 47, 94, -68, 101, -54, -119, 15, 30, 60, 120, -16, -3, -25, -45, -69, 107, -42, -79, 127, -2, -31, -33, -93, 91, -74, 113, -30, -39, -81, 67, -122, 17, 34, 68, -120, 13, 26, 52, 104, -48, -67, 103, -50, -127, 31, 62, 124, -8, -19, -57, -109, 59, 118, -20, -59, -105, 51, 102, -52, -123, 23, 46, 92, -72, 109, -38, -87, 79, -98, 33, 66, -124, 21, 42, 84, -88, 77, -102, 41, 82, -92, 85, -86, 73, -110, 57, 114, -28, -43, -73, 115, -26, -47, -65, 99, -58, -111, 63, 126, -4, -27, -41, -77, 123, -10, -15, -1, -29, -37, -85, 75, -106, 49, 98, -60, -107, 55, 110, -36, -91, 87, -82, 65, -126, 25, 50, 100, -56, -115, 7, 14, 28, 56, 112, -32, -35, -89, 83, -90, 81, -94, 89, -78, 121, -14, -7, -17, -61, -101, 43, 86, -84, 69, -118, 9, 18, 36, 72, -112, 61, 122, -12, -11, -9, -13, -5, -21, -53, -117, 11, 22, 44, 88, -80, 125, -6, -23, -49, -125, 27, 54, 108, -40, -83, 71, -114};
    public static byte[][] MULTIPLICATION_TABLE = Galois.generateMultiplicationTable();

    public static byte add(byte a, byte b) {
        return (byte)(a ^ b);
    }

    public static byte subtract(byte a, byte b) {
        return (byte)(a ^ b);
    }

    public static byte multiply(byte a, byte b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        short logA = LOG_TABLE[a & 0xFF];
        short logB = LOG_TABLE[b & 0xFF];
        int logResult = logA + logB;
        return EXP_TABLE[logResult];
    }

    public static byte divide(byte a, byte b) {
        if (a == 0) {
            return 0;
        }
        if (b == 0) {
            throw new IllegalArgumentException("Argument 'divisor' is 0");
        }
        short logA = LOG_TABLE[a & 0xFF];
        short logB = LOG_TABLE[b & 0xFF];
        int logResult = logA - logB;
        if (logResult < 0) {
            logResult += 255;
        }
        return EXP_TABLE[logResult];
    }

    public static byte exp(byte a, int n) {
        int logResult;
        if (n == 0) {
            return 1;
        }
        if (a == 0) {
            return 0;
        }
        short logA = LOG_TABLE[a & 0xFF];
        for (logResult = logA * n; 255 <= logResult; logResult -= 255) {
        }
        return EXP_TABLE[logResult];
    }

    public static short[] generateLogTable(int polynomial) {
        short[] result = new short[256];
        for (int i = 0; i < 256; ++i) {
            result[i] = -1;
        }
        int b = 1;
        for (int log = 0; log < 255; ++log) {
            if (result[b] != -1) {
                throw new RuntimeException("BUG: duplicate logarithm (bad polynomial?)");
            }
            result[b] = (short)log;
            if (256 > (b <<= 1)) continue;
            b = b - 256 ^ polynomial;
        }
        return result;
    }

    public static byte[] generateExpTable(short[] logTable) {
        byte[] result = new byte[510];
        for (int i = 1; i < 256; ++i) {
            short log = logTable[i];
            result[log] = (byte)i;
            result[log + 256 - 1] = (byte)i;
        }
        return result;
    }

    public static byte[][] generateMultiplicationTable() {
        byte[][] result = new byte[256][256];
        for (int a = 0; a < 256; ++a) {
            for (int b = 0; b < 256; ++b) {
                result[a][b] = Galois.multiply((byte)a, (byte)b);
            }
        }
        return result;
    }

    public static Integer[] allPossiblePolynomials() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < 256; ++i) {
            try {
                Galois.generateLogTable(i);
                result.add(i);
                continue;
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        return result.toArray(new Integer[result.size()]);
    }
}

