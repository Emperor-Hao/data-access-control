package com.kele.dataaccesscontrol;

import com.kele.dataaccesscontrol.common.LoginUser;
import com.kele.dataaccesscontrol.common.LoginUserHolder;
import com.kele.dataaccesscontrol.component.LoginUserLoader;
import com.kele.dataaccesscontrol.entity.SysContract;
import com.kele.dataaccesscontrol.entity.SysUser;
import com.kele.dataaccesscontrol.service.SysContractService;
import com.kele.dataaccesscontrol.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 数据权限集成测试：
 * 逐个模拟登录用户，验证其能看到的用户/合同集合。
 * 前置条件：本地 MySQL 已启动，且已执行 sql/init.sql（或等价建库语句）。
 */
@SpringBootTest
class DataScopeTest {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysContractService sysContractService;

    @Autowired
    private LoginUserLoader loginUserLoader;

    @Test
    void dataScopeFiltering() {
        // admin 全部数据权限
        assertVisibleUsers(1L, Set.of("admin", "zhangsan", "lisi", "wangwu", "aming", "aqiang", "aliang", "ahong", "ahua",
                "xiaoming", "xiaoli", "xiaowang", "xiaozhao", "xiaosun", "xiaozhou", "xiaowu", "xiaoqian",
                "xiaoliang", "xiaosong", "xiaotang", "xiaoxu", "xiaohu", "xiaoyang", "xiaogao", "xiaolin"));
        // 张三 本部门(102 后端组)
        assertVisibleUsers(2L, Set.of("zhangsan", "aqiang", "aliang", "xiaosun", "xiaozhou", "xiaowu", "xiaoqian"));
        // 李四 本部门及以下(200 销售中心 + 201/202)
        assertVisibleUsers(3L, Set.of("lisi", "ahong", "ahua", "xiaoliang", "xiaosong", "xiaotang", "xiaoxu",
                "xiaohu", "xiaoyang", "xiaogao", "xiaolin"));
        // 王五 仅本人
        assertVisibleUsers(4L, Set.of("wangwu"));
    }

    /** 合同表：同一个 @DataScope 复用到另一张业务表，验证权限逻辑可复用 */
    @Test
    void contractDataScopeFiltering() {
        // admin 全部合同 25 份
        assertVisibleContracts(1L, Set.of("HT-2026-001", "HT-2026-002", "HT-2026-003", "HT-2026-004",
                "HT-2026-005", "HT-2026-006", "HT-2026-007", "HT-2026-008", "HT-2026-009", "HT-2026-010",
                "HT-2026-011", "HT-2026-012", "HT-2026-013", "HT-2026-014", "HT-2026-015", "HT-2026-016",
                "HT-2026-017", "HT-2026-018", "HT-2026-019", "HT-2026-020", "HT-2026-021", "HT-2026-022",
                "HT-2026-023", "HT-2026-024", "HT-2026-025"));
        // 张三 本部门(102 后端组)：002/003、013~016
        assertVisibleContracts(2L, Set.of("HT-2026-002", "HT-2026-003", "HT-2026-013", "HT-2026-014", "HT-2026-015", "HT-2026-016"));
        // 李四 本部门及以下(200 销售中心 + 201/202)：004~007、017~025
        assertVisibleContracts(3L, Set.of("HT-2026-004", "HT-2026-005", "HT-2026-006", "HT-2026-007", "HT-2026-017",
                "HT-2026-018", "HT-2026-019", "HT-2026-020", "HT-2026-021", "HT-2026-022", "HT-2026-023",
                "HT-2026-024", "HT-2026-025"));
        // 王五 仅本人：仅自己创建的 008
        assertVisibleContracts(4L, Set.of("HT-2026-008"));
    }

    private void assertVisibleUsers(Long loginUserId, Set<String> expectedUserNames) {
        LoginUser loginUser = loginUserLoader.load(loginUserId);
        LoginUserHolder.set(loginUser);
        try {
            List<String> actual = sysUserService.selectUserList(new SysUser(), 1, 100).getRows().stream()
                    .map(SysUser::getUserName)
                    .toList();
            assertEquals(expectedUserNames, Set.copyOf(actual),
                    "用户[" + loginUser.getNickName() + "]的用户数据权限结果不符");
        } finally {
            LoginUserHolder.clear();
        }
    }

    private void assertVisibleContracts(Long loginUserId, Set<String> expectedContractNos) {
        LoginUser loginUser = loginUserLoader.load(loginUserId);
        LoginUserHolder.set(loginUser);
        try {
            List<String> actual = sysContractService.selectContractList(new SysContract(), 1, 100).getRows().stream()
                    .map(SysContract::getContractNo)
                    .toList();
            assertEquals(expectedContractNos, Set.copyOf(actual),
                    "用户[" + loginUser.getNickName() + "]的合同数据权限结果不符");
        } finally {
            LoginUserHolder.clear();
        }
    }
}
