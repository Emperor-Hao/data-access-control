package com.kele.dataaccesscontrol.service;

import com.kele.dataaccesscontrol.entity.SysDept;
import com.kele.dataaccesscontrol.mapper.SysDeptMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    public SysDeptService(SysDeptMapper sysDeptMapper) {
        this.sysDeptMapper = sysDeptMapper;
    }

    /**
     * 部门树：一次查出全部部门，在内存中按 parentId 组装成树。
     */
    public List<SysDept> selectDeptTree() {
        List<SysDept> all = sysDeptMapper.selectDeptList(new SysDept());
        return buildTree(all, 0L);
    }

    private List<SysDept> buildTree(List<SysDept> all, Long parentId) {
        List<SysDept> tree = new ArrayList<>();
        for (SysDept dept : all) {
            if (parentId.equals(dept.getParentId())) {
                dept.setChildren(buildTree(all, dept.getDeptId()));
                tree.add(dept);
            }
        }
        return tree;
    }
}
