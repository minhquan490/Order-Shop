import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var provider = new SslContextProvider("/home/quan/projects/backend/gitlab/order-shop/web/src/main/resources/localhost.pem", "/home/quan/projects/backend/gitlab/order-shop/web/src/main/resources/localhost-key.pem");
        EventLoopGroup group = new EpollEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
                    .channel(EpollServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(provider.createSslContext().newHandler(ch.alloc()), Http2Util.getServerAPNHandler());
                        }
                    });
            Channel ch = b.bind("localhost", 8080).sync().channel();

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
