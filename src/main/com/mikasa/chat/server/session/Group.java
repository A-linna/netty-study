package com.mikasa.chat.server.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author aiLun
 * @date 2023/5/30-16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Group {
    private String name;
    private Set<String> members;
}
