package com.kele.dataaccesscontrol.service;

import com.kele.dataaccesscontrol.annotation.DataScope;
import com.kele.dataaccesscontrol.entity.SysUser;
import com.kele.dataaccesscontrol.mapper.SysUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kele.dataaccesscontrol.common.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 用户列表：标注 @DataScope 后，查询前自动注入数据权限片段，
     * 不同登录用户看到的行数不同。
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    public PageResult<SysUser> selectUserList(SysUser query, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageResult.of(new PageInfo<>(sysUserMapper.selectUserList(query)));
    }
}
