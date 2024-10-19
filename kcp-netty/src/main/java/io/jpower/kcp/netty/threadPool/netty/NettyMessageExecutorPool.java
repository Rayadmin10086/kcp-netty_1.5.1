package io.jpower.kcp.netty.threadPool.netty;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.atomic.AtomicInteger;
import kcp.highway.threadPool.IMessageExecutor;
import kcp.highway.threadPool.IMessageExecutorPool;

public class NettyMessageExecutorPool implements IMessageExecutorPool {
   private EventLoopGroup eventExecutors;
   protected static final AtomicInteger index = new AtomicInteger();

   public NettyMessageExecutorPool(int workSize) {
      this.eventExecutors = new DefaultEventLoopGroup(workSize, r -> new Thread(r, "nettyMessageExecutorPool-" + index.incrementAndGet()));
   }

   public IMessageExecutor getIMessageExecutor() {
      return new NettyMessageExecutor(this.eventExecutors.next());
   }

   public void stop() {
      if (!this.eventExecutors.isShuttingDown()) {
         this.eventExecutors.shutdownGracefully();
      }
   }
}
