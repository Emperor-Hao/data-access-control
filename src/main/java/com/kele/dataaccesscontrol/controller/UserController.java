package com.kele.dataaccesscontrol.controller;

import com.kele.dataaccesscontrol.common.LoginUserHolder;
import com.kele.dataaccesscontrol.common.Result;
import com.kele.dataaccesscontrol.common.PageResult;
import com.kele.dataaccesscontrol.entity.SysUser;
import com.kele.dataaccesscontrol.service.SysUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final SysUserService sysUserService;

    public UserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 用户列表：展示数据权限效果。
     * 通过请求头 X-User-Id 或参数 userId 切换登录视角。
     */
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(SysUser query,
                                            Integer pageNum,
                                            Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        return Result.ok(sysUserService.selectUserList(query, safePageNum, safePageSize));
    }

    /** 当前登录用户信息 */
    @GetMapping("/me")
    public Result<?> me() {
        return Result.ok(LoginUserHolder.get());
    }
}
