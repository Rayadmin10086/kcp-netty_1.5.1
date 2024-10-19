package io.jpower.kcp.netty.threadPool.netty;

import io.netty.channel.EventLoop;
import io.jpower.kcp.netty.threadPool.IMessageExecutor;
import io.jpower.kcp.netty.threadPool.ITask;

public class NettyMessageExecutor implements IMessageExecutor {
   private EventLoop eventLoop;

   public NettyMessageExecutor(EventLoop eventLoop) {
      this.eventLoop = eventLoop;
   }

   public void stop() {
   }

   public boolean isFull() {
      return false;
   }

   public void execute(ITask iTask) {
      this.eventLoop.execute(() -> iTask.execute());
   }
}
