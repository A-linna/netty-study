package com.mikasa.chat.server.session;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author aiLun
 * @date 2023/5/31-10:24
 */
public class GroupSessionMemoryImpl implements GroupSession{
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();
    @Override
    public Group createGroup(String name, Set<String> members) {
        Group group = groupMap.get(name);
        if (Objects.nonNull(group)) {
            return null;
        }
        group = new Group(name, members);
        groupMap.put(name, group);
        return group;
    }

    @Override
    public Group joinMember(String groupName, String member) {
        Group group = groupMap.get(groupName);
        if (Objects.isNull(group)) {
            return null;
        }
        Set<String> members = group.getMembers();
        if (members.contains(member)) {
            return group;
        }
        group.getMembers().add(member);
        return group;
    }

    @Override
    public Group removeGroup(String name,String member) {
        Group group = groupMap.get(name);
        if (Objects.isNull(group)) {
            return null;
        }
        group.getMembers().remove(member);
        return group;
    }

    @Override
    public Set<String> getMembers(String name) {
        Group group = groupMap.get(name);
        if (Objects.isNull(group)) {
            return Sets.newHashSet();
        }
        return group.getMembers();
    }

    @Override
    public List<Channel> getMembersChannels(String name) {
        Group group = groupMap.get(name);
        if (Objects.isNull(group)) {
            return Lists.newArrayList();
        }
        Set<String> members = group.getMembers();
        Session session = SessionFactory.getSession("memory");
        return members.stream().map(session::getChannel).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
