package com.kele.dataaccesscontrol.entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysRole {
    private Long roleId;
    private String roleName;
    private String roleKey;
    private String status;
    private List<Long> menuIds = new ArrayList<>();
}