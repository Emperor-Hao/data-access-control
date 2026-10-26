package com.kele.dataaccesscontrol.controller;

import com.kele.dataaccesscontrol.common.LoginUserHolder;
import com.kele.dataaccesscontrol.common.Result;
import com.kele.dataaccesscontrol.entity.SysMenu;
import com.kele.dataaccesscontrol.service.SysMenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器
 * <p>
 * 提供菜单树查询、菜单管理、保存、删除以及角色菜单授权等接口。
 * 所有管理类操作均需校验当前用户是否具备管理员权限。
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final SysMenuService service;

    public MenuController(SysMenuService service) {
        this.service = service;
    }

    /**
     * 查询当前用户可见的菜单树
     */
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        return Result.ok(service.visibleTree(LoginUserHolder.getId()));
    }

    /**
     * 获取菜单管理页面的数据（全部菜单树 + 角色列表）
     */
    @GetMapping("/manage")
    public Result<?> manage() {
        if (!service.isAdmin(LoginUserHolder.getId())) {
            return Result.fail("没有菜单管理权限");
        }
        return Result.ok(Map.of(
                "menus", service.allTree(),
                "roles", service.roles()
        ));
    }

    /**
     * 新增或更新菜单
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public Result<?> save(@RequestBody SysMenu menu) {
        if (!service.isAdmin(LoginUserHolder.getId())) {
            return Result.fail("没有菜单管理权限");
        }
        service.save(menu);
        return Result.ok(null);
    }

    /**
     * 删除菜单（需先删除子菜单）
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (!service.isAdmin(LoginUserHolder.getId())) {
            return Result.fail("没有菜单管理权限");
        }
        return service.delete(id) ? Result.ok(null) : Result.fail("请先删除子菜单");
    }

    /**
     * 为指定角色分配菜单权限
     */
    @PutMapping("/role/{roleId}")
    public Result<?> roleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        if (!service.isAdmin(LoginUserHolder.getId())) {
            return Result.fail("没有菜单管理权限");
        }
        service.saveRoleMenus(roleId, menuIds);
        return Result.ok(null);
    }
}