package com.kele.dataaccesscontrol.component;

import com.kele.dataaccesscontrol.common.LoginUser;
import com.kele.dataaccesscontrol.entity.SysUser;
import com.kele.dataaccesscontrol.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

/**
 * 加载当前登录用户信息（简化版：真实项目通常从 Token / Session / Security 上下文获取）。
 */
@Component
public class LoginUserLoader {

    private final SysUserMapper sysUserMapper;

    public LoginUserLoader(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public LoginUser load(Long userId) {
        SysUser user = sysUserMapper.selectUserById(userId);
        if (user == null) {
            return null;
        }
        return new LoginUser(
                user.getUserId(),
                user.getUserName(),
                user.getNickName(),
                user.getDataScope(),
                user.getDeptId(),
                sysUserMapper.selectDeptIdsByUserId(userId));
    }
}
