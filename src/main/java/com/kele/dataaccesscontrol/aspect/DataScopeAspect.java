package com.kele.dataaccesscontrol.aspect;

import com.kele.dataaccesscontrol.annotation.DataScope;
import com.kele.dataaccesscontrol.common.BaseEntity;
import com.kele.dataaccesscontrol.common.LoginUser;
import com.kele.dataaccesscontrol.common.LoginUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint joinPoint, DataScope dataScope) {
        handleDataScope(joinPoint, dataScope);
    }

    protected void handleDataScope(JoinPoint joinPoint, DataScope dataScope) {
        LoginUser loginUser = LoginUserHolder.get();
        if (loginUser == null) {
            log.warn("当前无登录用户，本次查询不做数据权限过滤");
            return;
        }
        String sqlString = buildDataScopeSql(loginUser, dataScope);
        log.debug("userId={} dataScope={} sqlString={}", loginUser.getUserId(), loginUser.getDataScope(), sqlString);

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof BaseEntity baseEntity) {
                baseEntity.getParams().put("dataScope", sqlString);
                break;
            }
        }
    }

    /**
     * 根据用户的数据权限级别生成 WHERE 片段（只由本方法内部拼串，不接外部输入，避免 SQL 注入）。
     */
    protected String buildDataScopeSql(LoginUser user, DataScope dataScope) {
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();
        int scope = user.getDataScope() == null ? 1 : user.getDataScope();

        return switch (scope) {
            // 3 本部门数据（用户可能属于多个部门）
            case 3 -> deptAlias + ".dept_id IN (SELECT dept_id FROM sys_user_dept WHERE user_id = " + user.getUserId() + ")";
            // 4 本部门及以下数据：部门是自己所在部门，或其祖先路径里包含自己所在部门（FIND_IN_SET 一步查子孙）
            case 4 -> deptAlias + ".dept_id IN (SELECT sd.dept_id FROM sys_dept sd WHERE EXISTS ("
                    + "SELECT 1 FROM sys_user_dept ud WHERE ud.user_id = " + user.getUserId()
                    + " AND (sd.dept_id = ud.dept_id OR FIND_IN_SET(ud.dept_id, sd.ancestors))))";
            // 5 仅本人数据
            case 5 -> userAlias + ".user_id = " + user.getUserId();
            // 1 全部数据权限
            default -> "1 = 1";
        };
    }
}
