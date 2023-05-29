package com.mikasa.netty.EventLoop;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author aiLun
 * @date 2023/5/28-16:54
 */
@Slf4j
public class EventLoopGroupDemo {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2);//可以处理io事件，普通任务。定时任务
      //  DefaultEventLoop eventLoopGroup = new DefaultEventLoop();//普通任务。定时任务
        //获取下一个eventLoop
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //执行普通方法
        group.next().execute(()->{
            log.info("ok");
        });
      log.info("main ");

      //定时任务
        group.next().scheduleAtFixedRate(() -> {
            log.info("schedule");
        }, 2, 2, TimeUnit.SECONDS);
    }
}
