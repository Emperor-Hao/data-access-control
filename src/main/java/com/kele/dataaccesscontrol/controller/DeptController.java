package com.kele.dataaccesscontrol.controller;

import com.kele.dataaccesscontrol.common.Result;
import com.kele.dataaccesscontrol.entity.SysDept;
import com.kele.dataaccesscontrol.service.SysDeptService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
public class DeptController {

    private final SysDeptService sysDeptService;

    public DeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

    /** 部门树 */
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        return Result.ok(sysDeptService.selectDeptTree());
    }
}
