package io.jpower.kcp.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

class KcpServer$idk extends ChannelInitializer<Channel> {
   KcpServer$idk(KcpServer this$0, ChannelConfig var2, KcpListener var3) {
      this.this$0 = this$0;
      this.val$channelConfig = var2;
      this.val$kcpListener = var3;
   }

   protected void initChannel(Channel ch) {
      ServerChannelHandler serverChannelHandler = new ServerChannelHandler(
         this.this$0.channelManager, this.val$channelConfig, this.this$0.iMessageExecutorPool, this.val$kcpListener, this.this$0.hashedWheelTimer
      );
      ChannelPipeline cp = ch.pipeline();
      if (this.val$channelConfig.isCrc32Check()) {
         Crc32Encode crc32Encode = new Crc32Encode();
         Crc32Decode crc32Decode = new Crc32Decode();
         cp.addLast(new ChannelHandler[]{crc32Encode});
         cp.addLast(new ChannelHandler[]{crc32Decode});
      }

      cp.addLast(new ChannelHandler[]{serverChannelHandler});
   }
}
