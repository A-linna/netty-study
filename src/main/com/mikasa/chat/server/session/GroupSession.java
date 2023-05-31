package com.mikasa.chat.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * @author aiLun
 * @date 2023/5/30-16:47
 */
public interface GroupSession {

    /**
     * 创建聊天组，
     * @param name 组名 不能重复
     * @param members 成员
     * @return
     */
    Group createGroup(String name, Set<String> members);

    /**
     * 加入聊天组
     * @param groupName 组名
     * @param member 成员
     * @return
     */
    Group joinMember(String groupName, String member);

    /**
     * 移除聊天组
     * @param name
     * @return
     */
    Group removeGroup(String name,String member);

    Set<String> getMembers(String name);


    /**
     * 获取成员channel集合
     * @param name
     * @return
     */
    List<Channel> getMembersChannels(String name);
}
