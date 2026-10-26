package com.kele.dataaccesscontrol.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体基类：dataScope 权限片段通过 params 透传到 Mapper XML。
 */
@Data
public class BaseEntity {
    private Map<String, Object> params = new HashMap<>();
}
