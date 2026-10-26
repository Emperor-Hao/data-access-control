package com.kele.dataaccesscontrol.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户上下文（简化版，真实项目通常来自 Spring Security）。
 */
@Data
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private String userName;
    private String nickName;
    /** 数据权限 1全部 / 3本部门 / 4本部门及以下 / 5仅本人 */
    private Integer dataScope;
    /** 主部门 */
    private Long deptId;
    /** 所属所有部门 */
    private List<Long> deptIds;
}
