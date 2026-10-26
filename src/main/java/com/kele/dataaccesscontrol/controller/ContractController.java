package com.kele.dataaccesscontrol.controller;

import com.kele.dataaccesscontrol.common.Result;
import com.kele.dataaccesscontrol.common.PageResult;
import com.kele.dataaccesscontrol.entity.SysContract;
import com.kele.dataaccesscontrol.service.SysContractService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    private final SysContractService sysContractService;

    public ContractController(SysContractService sysContractService) {
        this.sysContractService = sysContractService;
    }

    /**
     * 合同列表：展示数据权限过滤后的查询结果。
     * 通过请求头 X-User-Id 或参数 userId 切换登录视角。
     */
    @GetMapping("/list")
    public Result<PageResult<SysContract>> list(SysContract query,
                                                Integer pageNum,
                                                Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        return Result.ok(sysContractService.selectContractList(query, safePageNum, safePageSize));
    }
}
