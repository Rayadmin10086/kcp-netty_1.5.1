package io.jpower.kcp.netty.threadPool;

public interface IMessageExecutorPool {
   IMessageExecutor getIMessageExecutor();

   void stop();
}
