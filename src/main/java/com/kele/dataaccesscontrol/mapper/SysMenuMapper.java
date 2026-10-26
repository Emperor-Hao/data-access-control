package com.kele.dataaccesscontrol.mapper;

import com.kele.dataaccesscontrol.entity.SysMenu;
import com.kele.dataaccesscontrol.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单管理 Mapper
 * <p>
 * 提供菜单树查询、菜单增删改、角色查询以及角色-菜单关联操作。
 */
@Mapper
public interface SysMenuMapper {

    /**
     * 查询指定用户可见的菜单列表
     *
     * @param userId 用户 ID
     */
    List<SysMenu> selectMenuList(Long userId);

    /**
     * 查询全部菜单列表
     */
    List<SysMenu> selectAllMenuList();

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleList();

    /**
     * 查询指定角色已授权的菜单 ID 列表
     *
     * @param roleId 角色 ID
     */
    List<Long> selectRoleMenuIds(Long roleId);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 更新菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     *
     * @param menuId 菜单 ID
     */
    int deleteMenu(Long menuId);

    /**
     * 删除指定角色的所有菜单授权关系
     *
     * @param roleId 角色 ID
     */
    int deleteRoleMenus(Long roleId);

    /**
     * 新增角色与菜单的授权关系
     *
     * @param roleId 角色 ID
     * @param menuId 菜单 ID
     */
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /**
     * 统计指定菜单的子菜单数量
     *
     * @param menuId 菜单 ID
     */
    int countChildMenus(Long menuId);

    /**
     * 统计用户拥有管理员角色的数量
     *
     * @param userId 用户 ID
     */
    int countUserAdminRole(Long userId);
}