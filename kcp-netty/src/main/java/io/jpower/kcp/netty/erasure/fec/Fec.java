package io.jpower.kcp.netty.erasure.fec;

public class Fec {
   public static int fecHeaderSize = 6;
   public static int fecDataSize = 2;
   public static int fecHeaderSizePlus2;
   public static int typeData;
   public static int typeParity;

   static {
      fecHeaderSizePlus2 = fecHeaderSize + fecDataSize;
      typeData = 241;
      typeParity = 242;
   }
}
