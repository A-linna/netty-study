package com.mikasa.chat.server.session;


import io.netty.channel.Channel;

/**
 * 会话管理接口
 *
 * @author aiLun
 * @date 2023/5/30-16:38
 */
public interface Session {

    /**
     * 绑定会话
     * @param channel
     * @param userName
     */
    void bind(Channel channel, String userName);

    /**
     * 解绑会话
     * @param channel
     */
    void unbind(Channel channel);

    /**
     * 获取属性
     * @param channel
     * @param name
     * @return
     */
    Object getAttribute(Channel channel, String name);

    /**
     * 设置属性
     * @param channel
     * @param key
     * @param value
     */
    void setAttribute(Channel channel, String key,String value);

    /**
     * 获取用户的channel
     * @param userName
     * @return
     */
    Channel getChannel(String userName);
}
