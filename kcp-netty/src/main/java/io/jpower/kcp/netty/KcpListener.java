package io.jpower.kcp.netty;

import io.netty.buffer.ByteBuf;

public interface KcpListener {
   void onConnected(Ukcp var1);

   void handleReceive(ByteBuf var1, Ukcp var2);

   void handleException(Throwable var1, Ukcp var2);

   void handleClose(Ukcp var1);
}
