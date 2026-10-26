package com.kele.dataaccesscontrol.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysDept {

    private Long deptId;
    /** 父部门id，0 表示根 */
    private Long parentId;
    /** 祖级列表，如 0,100,101 */
    private String ancestors;
    private String deptName;
    private Integer orderNum;
    private String status;
    private String delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 子部门，用于构建部门树 */
    private List<SysDept> children = new ArrayList<>();
}
