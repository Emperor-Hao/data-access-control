package com.kele.dataaccesscontrol.service;

import com.kele.dataaccesscontrol.annotation.DataScope;
import com.kele.dataaccesscontrol.entity.SysContract;
import com.kele.dataaccesscontrol.mapper.SysContractMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kele.dataaccesscontrol.common.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysContractService {

    private final SysContractMapper sysContractMapper;

    public SysContractService(SysContractMapper sysContractMapper) {
        this.sysContractMapper = sysContractMapper;
    }

    /**
     * 合同列表：与用户列表使用完全相同的 @DataScope 注解，
     * 切面自动根据当前登录用户拼出权限片段注入 params.dataScope。
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    public PageResult<SysContract> selectContractList(SysContract query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageResult.of(new PageInfo<>(sysContractMapper.selectContractList(query)));
    }
}
