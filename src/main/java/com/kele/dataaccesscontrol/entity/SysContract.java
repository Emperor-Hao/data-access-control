package com.kele.dataaccesscontrol.entity;

import com.kele.dataaccesscontrol.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysContract extends BaseEntity {

    private Long contractId;
    private String contractNo;
    private String contractName;
    private BigDecimal amount;
    /** 归属部门id */
    private Long deptId;
    /** 创建人id */
    private Long createBy;
    private String delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /** 冗余字段，仅用于列表展示 */
    private String deptName;
    private String createByName;
}
