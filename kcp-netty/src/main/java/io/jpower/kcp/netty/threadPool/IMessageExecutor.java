package io.jpower.kcp.netty.threadPool;

public interface IMessageExecutor {
   void stop();

   boolean isFull();

   void execute(ITask var1);
}
