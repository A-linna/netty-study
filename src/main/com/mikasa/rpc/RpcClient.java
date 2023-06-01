package com.mikasa.rpc;

import com.mikasa.rpc.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aiLun
 * @date 2023/5/31-20:18
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        HelloService service = RpcClientManager.getService(HelloService.class);
        System.out.println(service.say("我是你爹"));
       // System.out.println(service.say("张三"));

    }
}
