package io.jpower.kcp.netty.threadPool.disruptor;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import io.jpower.kcp.netty.threadPool.IMessageExecutor;
import io.jpower.kcp.netty.threadPool.IMessageExecutorPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisruptorExecutorPool implements IMessageExecutorPool {
   private static final Logger log = LoggerFactory.getLogger(DisruptorExecutorPool.class);
   protected List<IMessageExecutor> executor = new Vector<>();
   protected AtomicInteger index = new AtomicInteger();

   public DisruptorExecutorPool(int workSize) {
      for (int i = 0; i < workSize; i++) {
         this.createDisruptorProcessor("DisruptorExecutorPool-" + i);
      }
   }

   private IMessageExecutor createDisruptorProcessor(String threadName) {
      DisruptorSingleExecutor singleProcess = new DisruptorSingleExecutor(threadName);
      this.executor.add(singleProcess);
      singleProcess.start();
      return singleProcess;
   }

   public void stop() {
      for (IMessageExecutor process : this.executor) {
         process.stop();
      }
   }

   public IMessageExecutor getIMessageExecutor() {
      int index = this.index.incrementAndGet();
      return this.executor.get(index % this.executor.size());
   }
}
