-- 每次启动重建演示数据，方便反复演示（仅用于 demo）
-- 旧库可能存在历史外键，关闭检查后再清理，避免残留表导致重复建表失败
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS sys_contract;
DROP TABLE IF EXISTS sys_user_dept;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_dept;

-- 菜单权限
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS sys_dept (
    dept_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门id',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父部门id',
    ancestors   VARCHAR(500) NOT NULL DEFAULT '' COMMENT '祖级列表(如 0,100,101)',
    dept_name   VARCHAR(64)  NOT NULL COMMENT '部门名称',
    order_num   INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    status      CHAR(1)      NOT NULL DEFAULT '0' COMMENT '部门状态(0正常 1停用)',
    del_flag    CHAR(1)      NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (dept_id)
) ENGINE=InnoDB COMMENT='部门表';

CREATE TABLE IF NOT EXISTS sys_user (
    user_id     BIGINT      NOT NULL AUTO_INCREMENT COMMENT '用户id',
    dept_id     BIGINT      DEFAULT NULL COMMENT '主部门id',
    user_name   VARCHAR(64) NOT NULL COMMENT '登录账号',
    nick_name   VARCHAR(64) NOT NULL COMMENT '用户昵称',
    data_scope  TINYINT     NOT NULL DEFAULT 1 COMMENT '数据权限(1全部 3本部门 4本部门及以下 5仅本人)',
    status      CHAR(1)     NOT NULL DEFAULT '0' COMMENT '账号状态(0正常 1停用)',
    del_flag    CHAR(1)     NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (user_id)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_user_dept (
    user_id BIGINT NOT NULL COMMENT '用户id',
    dept_id BIGINT NOT NULL COMMENT '部门id',
    PRIMARY KEY (user_id, dept_id)
) ENGINE=InnoDB COMMENT='用户-部门关联表';


-- 业务数据表：每笔合同归属部门 + 创建人，是数据权限过滤的查询目标
CREATE TABLE IF NOT EXISTS sys_contract (
    contract_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '合同id',
    contract_no   VARCHAR(32)   NOT NULL COMMENT '合同编号',
    contract_name VARCHAR(128)  NOT NULL COMMENT '合同名称',
    amount        DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '合同金额',
    dept_id       BIGINT        DEFAULT NULL COMMENT '归属部门id',
    create_by     BIGINT        DEFAULT NULL COMMENT '创建人id',
    del_flag      CHAR(1)       NOT NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
    create_time   DATETIME      DEFAULT NULL COMMENT '创建时间',
    update_time   DATETIME      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (contract_id)
) ENGINE=InnoDB COMMENT='合同表';


-- 菜单管理
CREATE TABLE IF NOT EXISTS sys_role (
                          role_id     BIGINT       NOT NULL AUTO_INCREMENT,
                          role_name   VARCHAR(64)  NOT NULL,
                          role_key    VARCHAR(64)  NOT NULL,
                          status      CHAR(1)      NOT NULL DEFAULT '0',
                          PRIMARY KEY (role_id), UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_menu (
                          menu_id     BIGINT       NOT NULL AUTO_INCREMENT,
                          parent_id   BIGINT       NOT NULL DEFAULT 0,
                          menu_name   VARCHAR(64)  NOT NULL,
                          path        VARCHAR(128) NOT NULL DEFAULT '',
                          component   VARCHAR(128) DEFAULT NULL,
                          menu_type   CHAR(1)      NOT NULL DEFAULT 'C' COMMENT 'M目录 C菜单',
                          icon        VARCHAR(32)  DEFAULT NULL,
                          order_num   INT          NOT NULL DEFAULT 0,
                          status      CHAR(1)      NOT NULL DEFAULT '0',
                          PRIMARY KEY (menu_id)
) ENGINE=InnoDB COMMENT='菜单表';

CREATE TABLE IF NOT EXISTS sys_user_role (
                               user_id BIGINT NOT NULL, role_id BIGINT NOT NULL,
                               PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_role_menu (
                               role_id BIGINT NOT NULL, menu_id BIGINT NOT NULL,
                               PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色菜单关联表';
