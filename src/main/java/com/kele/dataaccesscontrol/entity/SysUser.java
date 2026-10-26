package com.kele.dataaccesscontrol.entity;

import com.kele.dataaccesscontrol.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private Long userId;
    /** 主部门id */
    private Long deptId;
    private String userName;
    private String nickName;
    /** 数据权限 1全部 / 3本部门 / 4本部门及以下 / 5仅本人 */
    private Integer dataScope;
    private String status;
    private String delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 冗余字段：主部门名称，仅用于列表展示 */
    private String deptName;

    /** 角色名称，多个角色使用顿号分隔，仅用于列表展示 */
    private String roleNames;
}
