package com.mikasa.mychat.server.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aiLun
 * @date 2023/5/30-16:41
 */
public class SessionMemoryImpl implements Session {
    private final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, String> channelUserMap = new ConcurrentHashMap<>();
    private final Map<Channel, Map<String, Object>> channelAttributeMap = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel channel, String userName) {
        userChannelMap.put(userName, channel);
        channelUserMap.put(channel, userName);
        channelAttributeMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        String userName = channelUserMap.remove(channel);
        userChannelMap.remove(userName);
        channelAttributeMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributeMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String key,String value) {
        Map<String, Object> attribute = channelAttributeMap.get(channel);
        attribute.put(key, value);
    }

    @Override
    public Channel getChannel(String userName) {

        return userChannelMap.get(userName);
    }
}
