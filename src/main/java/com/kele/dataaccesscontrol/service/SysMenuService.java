package com.kele.dataaccesscontrol.service;

import com.kele.dataaccesscontrol.entity.SysMenu;
import com.kele.dataaccesscontrol.entity.SysRole;
import com.kele.dataaccesscontrol.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理服务
 * <p>
 * 负责菜单树构建、角色查询、权限校验以及角色菜单授权等业务逻辑。
 */
@Service
public class SysMenuService {

    private final SysMenuMapper mapper;

    public SysMenuService(SysMenuMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * 查询指定用户可见的菜单树
     */
    public List<SysMenu> visibleTree(Long userId) {
        return tree(mapper.selectMenuList(userId));
    }

    /**
     * 查询全部菜单树
     */
    public List<SysMenu> allTree() {
        return tree(mapper.selectAllMenuList());
    }

    /**
     * 查询所有角色，并填充每个角色已授权的菜单 ID 列表
     */
    public List<SysRole> roles() {
        List<SysRole> roles = mapper.selectRoleList();
        roles.forEach(role ->
                role.setMenuIds(mapper.selectRoleMenuIds(role.getRoleId()))
        );
        return roles;
    }

    /**
     * 判断用户是否为管理员
     */
    public boolean isAdmin(Long userId) {
        return mapper.countUserAdminRole(userId) > 0;
    }

    /**
     * 保存角色与菜单的授权关系（先删除旧关联，再插入新关联）
     */
    @Transactional
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        mapper.deleteRoleMenus(roleId);
        if (menuIds != null) {
            menuIds.forEach(id -> mapper.insertRoleMenu(roleId, id));
        }
    }

    /**
     * 新增或更新菜单
     */
    public void save(SysMenu menu) {
        if (menu.getMenuId() == null) {
            mapper.insertMenu(menu);
        } else {
            mapper.updateMenu(menu);
        }
    }

    /**
     * 删除菜单（存在子菜单时不允许删除）
     */
    public boolean delete(Long id) {
        if (mapper.countChildMenus(id) > 0) {
            return false;
        }
        return mapper.deleteMenu(id) > 0;
    }

    /**
     * 将扁平列表构建为菜单树（从根节点 parentId = 0 开始）
     */
    private List<SysMenu> tree(List<SysMenu> all) {
        return build(all, 0L);
    }

    /**
     * 递归构建菜单树
     *
     * @param all    全部菜单的扁平列表
     * @param parent 当前父节点 ID
     * @return 该父节点下的子树
     */
    private List<SysMenu> build(List<SysMenu> all, Long parent) {
        List<SysMenu> result = new ArrayList<>();
        all.stream()
                .filter(m -> parent.equals(m.getParentId()))
                .forEach(m -> {
                    m.setChildren(build(all, m.getMenuId()));
                    result.add(m);
                });
        return result;
    }
}