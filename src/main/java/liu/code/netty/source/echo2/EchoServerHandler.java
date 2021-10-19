/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package liu.code.netty.source.echo2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // group 就是充当业务线程池，可以将任务提交到该线程池
    // 这里我们创建了16个线程
    private final ExecutorService group = Executors.newFixedThreadPool(16);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("EchoServer Handler 的线程是=" + Thread.currentThread().getName());

        //按照原来的方法处理耗时任务

        //解决方案1 用户程序自定义的普通任务

//        ctx.channel().eventLoop().execute(() -> {
//            try {
//                Thread.sleep(1000);
//                //输出线程名
//                System.out.println("EchoServerHandler execute 线程是=" + Thread.currentThread().getName());
//                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵", CharsetUtil.UTF_8));
//
//            } catch (Exception ex) {
//                System.out.println("发生异常" + ex.getMessage());
//            }
//        });

        //将任务提交到 group线程池
//        group.submit(() -> {
//            //接收客户端信息
//            ByteBuf buf = (ByteBuf) msg;
//            byte[] bytes = new byte[buf.readableBytes()];
//            buf.readBytes(bytes);
//            String body = new String(bytes, StandardCharsets.UTF_8);
//            System.out.println(body);
//            //休眠10秒
//            Thread.sleep(3 * 1000);
//            System.out.println("group.submit 的  call 线程是=" + Thread.currentThread().getName());
//            ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵0", CharsetUtil.UTF_8));
//            return null;
//        });
//
//        System.out.println("readed...");
        //普通方式
        //接收客户端信息
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        //休眠10秒
        Thread.sleep(1000);
        System.out.println("普通调用方式的 线程是=" + Thread.currentThread().getName());
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)喵", CharsetUtil.UTF_8));

        System.out.println("go on ");

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("read completed...");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        ctx.close();
    }
}
