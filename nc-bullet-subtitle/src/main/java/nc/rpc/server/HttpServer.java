package nc.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {

    public void initializeServer(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        EventLoopGroup boot = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(8);
        bootstrap.group(boot, work);
        bootstrap.childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("http-decode", new HttpRequestDecoder());//解码request 1
                ch.pipeline().addLast("http-encode", new HttpResponseEncoder());// 编码response 3
                ch.pipeline().addLast("http-server-handler", new HttpServerHandler()); // 业务处理2
            }
        });
        try {
            ChannelFuture f = bootstrap.bind(8080).sync();
            System.out.println("服务启动成功：8080");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boot.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.initializeServer();
    }
}
