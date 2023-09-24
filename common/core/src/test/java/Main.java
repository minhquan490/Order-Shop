import com.bachlinh.order.core.server.netty.channel.http2.Http2ServerInitializer;
import com.bachlinh.order.core.server.netty.ssl.SslContextProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var provider = new SslContextProvider("/home/quan/projects/backend/gitlab/order-shop/web/src/main/resources/localhost.pem", "/home/quan/projects/backend/gitlab/order-shop/web/src/main/resources/localhost-key.pem");
        var initializer = new Http2ServerInitializer(null, provider);
        EpollEventLoopGroup loopGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(loopGroup)
                    .channel(EpollServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(initializer);
            Channel channel = bootstrap.bind("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
