package com.kele.dataaccesscontrol.entity;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenu {
    private Long menuId;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String menuType;
    private String icon;
    private Integer orderNum;
    private String status;
    private Boolean checked;
    private List<SysMenu> children = new ArrayList<>();
}