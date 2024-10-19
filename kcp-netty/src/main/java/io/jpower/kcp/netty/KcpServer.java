package io.jpower.kcp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.HashedWheelTimer;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import io.jpower.kcp.netty.KcpServer.idk;
import io.jpower.kcp.netty.KcpServer.TimerThreadFactory;
import io.jpower.kcp.netty.erasure.fec.Fec;
import io.jpower.kcp.netty.threadPool.IMessageExecutorPool;

public class KcpServer {
   private IMessageExecutorPool iMessageExecutorPool;
   private Bootstrap bootstrap;
   private EventLoopGroup group;
   private List<Channel> localAddresss = new Vector<>();
   private IChannelManager channelManager;
   private HashedWheelTimer hashedWheelTimer;

   public void init(KcpListener kcpListener, ChannelConfig channelConfig, InetSocketAddress... addresses) {
      if (channelConfig.isUseConvChannel()) {
         int convIndex = 0;
         if (channelConfig.getFecAdapt() != null) {
            convIndex += Fec.fecHeaderSizePlus2;
         }

         this.channelManager = new ServerConvChannelManager(convIndex);
      } else {
         this.channelManager = new ServerAddressChannelManager();
      }

      this.hashedWheelTimer = new HashedWheelTimer(new TimerThreadFactory(), 1L, TimeUnit.MILLISECONDS);
      boolean epoll = Epoll.isAvailable();
      boolean kqueue = KQueue.isAvailable();
      this.iMessageExecutorPool = channelConfig.getiMessageExecutorPool();
      this.bootstrap = new Bootstrap();
      int cpuNum = Runtime.getRuntime().availableProcessors();
      int bindTimes = 1;
      if (epoll || kqueue) {
         this.bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
         bindTimes = cpuNum;
      }

      Class<? extends Channel> channelClass = null;
      if (epoll) {
         this.group = new EpollEventLoopGroup(cpuNum);
         channelClass = EpollDatagramChannel.class;
      } else if (kqueue) {
         this.group = new KQueueEventLoopGroup(cpuNum);
         channelClass = KQueueDatagramChannel.class;
      } else {
         this.group = new NioEventLoopGroup(addresses.length);
         channelClass = NioDatagramChannel.class;
      }

      this.bootstrap.channel(channelClass);
      this.bootstrap.group(this.group);
      this.bootstrap.handler(new idk(this, channelConfig, kcpListener));
      this.bootstrap.option(ChannelOption.SO_REUSEADDR, true);

      for (InetSocketAddress addres : addresses) {
         for (int i = 0; i < bindTimes; i++) {
            ChannelFuture channelFuture = this.bootstrap.bind(addres);
            Channel channel = channelFuture.channel();
            this.localAddresss.add(channel);
         }
      }

      Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
   }

   public void stop() {
      this.localAddresss.forEach(ChannelOutboundInvoker::close);
      this.channelManager.getAll().forEach(Ukcp::close);
      if (this.iMessageExecutorPool != null) {
         this.iMessageExecutorPool.stop();
      }

      if (this.hashedWheelTimer != null) {
         this.hashedWheelTimer.stop();
      }

      if (this.group != null) {
         this.group.shutdownGracefully();
      }
   }

   public IChannelManager getChannelManager() {
      return this.channelManager;
   }
}
