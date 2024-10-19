package io.jpower.kcp.netty;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class KcpServer$TimerThreadFactory implements ThreadFactory {
   private AtomicInteger timeThreadName = new AtomicInteger(0);

   private KcpServer$TimerThreadFactory() {
   }

   @Override
   public Thread newThread(Runnable r) {
      return new Thread(r, "KcpServerTimerThread " + this.timeThreadName.addAndGet(1));
   }
}
