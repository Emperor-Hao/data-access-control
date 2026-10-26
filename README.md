# 数据权限控制实战（MyBatis DataScope）

演示基于 MyBatis 的"数据权限"控制：不同用户登录后，查询同一份数据，只能看到自己权限范围内的行。

## 目录

- [快速开始](#快速开始)
- [演示效果](#演示效果)
- [核心概念：parent_id + ancestors](#核心概念parent_id--ancestors)
- [为什么 DataScope 需要部门树](#为什么-datascope-需要部门树)
- [项目结构](#项目结构)

---

## 快速开始

技术栈：Spring Boot 4.1 + Java 21 + MyBatis + MySQL。

1. **准备数据库**（需要本地有 MySQL，默认账号密码 root/root，可用环境变量 `MYSQL_USER` / `MYSQL_PASSWORD` / `MYSQL_DB` 覆盖）：

   ```bash
   mysql -uroot -p < sql/init.sql
   ```

2. **启动应用**：启动时会自动执行 `schema.sql` + `data.sql` 重建演示数据。

   ```bash
   mvn spring-boot:run
   ```

3. **看数据权限效果**：通过请求头 `X-User-Id`（或参数 `userId`）切换登录视角：

   ```bash
   # admin 全部数据权限 → 9 人
   curl -H "X-User-Id: 1" "localhost:8080/api/user/list"
   # 张三 本部门(102 后端组) → 3 人
   curl -H "X-User-Id: 2" "localhost:8080/api/user/list"
   # 李四 本部门及以下(200 销售中心及其子孙) → 3 人
   curl -H "X-User-Id: 3" "localhost:8080/api/user/list"
   # 王五 仅本人 → 1 人
   curl -H "X-User-Id: 4" "localhost:8080/api/user/list"

   # 当前登录用户信息 & 部门树 & 合同列表
   curl -H "X-User-Id: 3" "localhost:8080/api/user/me"
   curl "localhost:8080/api/dept/tree"
   curl -H "X-User-Id: 3" "localhost:8080/api/contract/list"
   # 合同列表也支持按名称模糊过滤
   curl -H "X-User-Id: 1" "localhost:8080/api/contract/list?contractName=华东"
   ```

4. **跑测试验证**：

   ```bash
   mvn test
   ```

## 演示效果

部门树：

```
0 总公司
├── 100 研发中心
│   ├── 101 前端组
│   └── 102 后端组
└── 200 销售中心
    ├── 201 华东销售
    └── 202 华南销售
```

| 登录用户 | data_scope | 能看到的人 | 数量 |
|---|---|---|---|
| admin | 1 全部 | 全部 9 人 | 9 |
| 张三 | 3 本部门 | 本部门(102 后端组) 3 人 | 3 |
| 李四 | 4 本部门及以下 | 销售中心及下属 3 人 | 3 |
| 王五 | 5 仅本人 | 仅自己 1 人 | 1 |

### 合同表：同一个注解，换一张业务表

`sys_contract`（合同表）是第二张被数据权限过滤的业务表，用法和用户列表**完全一样**：
Service 方法上同样标注 `@DataScope(deptAlias="d", userAlias="u")`，XML 里同样写 `${params.dataScope}`。

| 登录用户 | data_scope | 能看到的合同 | 数量 |
|---|---|---|---|
| admin | 1 全部 | 全部 8 份 | 8 |
| 张三 | 3 本部门(102) | HT-2026-002 / 003 | 2 |
| 李四 | 4 本部门及以下(200) | HT-2026-004 ~ 007 | 4 |
| 王五 | 5 仅本人 | 仅自己创建的 HT-2026-008 | 1 |

这正是注解 + 切面的价值：**权限逻辑只写一遍，任意业务表复用**。

---

## 核心概念：parent_id + ancestors

这是存储"树形部门层级"的两种字段，解决的是同一个问题：**某个部门在树里的位置**。

### `parent_id` —— 只记"我的爹是谁"（单向指向父节点）

```
0 总公司
└── 100 研发中心        (parent_id = 0)
    ├── 101 前端组      (parent_id = 100)
    └── 102 后端组      (parent_id = 100)
└── 200 销售中心        (parent_id = 0)
    └── 201 华东销售    (parent_id = 200)
```

每行只存一行。用 `101 前端组` 举例：

- `parent_id = 100` → 它的父部门是 100

**要回答"100 研发中心下有哪些子孙部门"**，用 `parent_id` 需要递归：先查 100 的直接子节点，再查子节点的子节点……部门深几层就查几次 SQL。

### `ancestors` —— 直接记"我一路的祖先是谁"（冗余路径）

```
dept_id  dept_name    parent_id  ancestors
100      研发中心       0        0,100
101      前端组         100       0,100,101
102      后端组         100       0,100,102
200      销售中心       0        0,200
201      华东销售       200       0,200,201
```

`101 前端组` 的 `ancestors = 0,100,101`，意思是从根走到自己的完整链条。**根 `0` 在前面固定带上**，所以 `ancestors` 必然以 `0,` 开头。

这是**空间换时间**的冗余设计：

- 树变化时需要维护 `ancestors`（新增/移动节点要更新它）
- 好处是**查子孙一步到位**，不用递归

### 它如何服务"本部门及以下"数据权限

用 SQL 的 `FIND_IN_SET(100, ancestors)`，含义是"**值 100 是否出现在这段逗号路径里**"：

```sql
FIND_IN_SET(100, '0,100,101')  →  2   -- 101 的祖先路径里有 100，说明 101 是 100 的子孙
FIND_IN_SET(100, '0,100,102')  →  2   -- 102 同理
FIND_IN_SET(100, '0,200,201')  →  0   -- 201 的路径里没有 100，不是它的子孙
```

于是"研发中心及以下"就一句话：

```sql
WHERE dept_id = 100 OR FIND_IN_SET(100, ancestors)
```

不需要递归、不需要自连接，一条 SQL 全查出来。`0` 在头部还有个好处：`FIND_IN_SET(0, ancestors)` 恒成立（路径都以 0 开头），所以 `dept_id = 0` 的根部门天然属于所有祖先关系，边界不出错。

> 提示：`FIND_IN_SET` 是 MySQL 函数。换 PostgreSQL 需用 `ANY(string_to_array(...))`，换 H2（MySQL 兼容模式）也有 `FIND_IN_SET`。这是 demo 选用 MySQL 的一个理由。

## 为什么 DataScope 需要部门树

数据权限的几个典型级别，都和部门树直接相关：

| 级别 | 含义 | 需要部门树吗 |
|---|---|---|
| 全部数据 | `1 = 1` | 否 |
| 本部门数据 | 只看自己部门 | 只需要知道"我的部门" |
| **本部门及以下数据** | 看自己部门 + 所有子孙部门 | **需要，`ancestors` 就是为此而生** |
| 仅本人数据 | 只看自己 | 否 |

其中"本部门及以下"最常见也最依赖部门树：用户属于 200 销售中心，就要能看到 200、201、202 的全部数据，靠 `FIND_IN_SET` 一网打尽。

## 项目结构

```
src/main/java/com/kele/dataaccesscontrol/
├── DataAccessControlApplication.java   # 启动类 @MapperScan
├── annotation/DataScope.java          # 数据权限注解
├── aspect/DataScopeAspect.java        # AOP 切面：拼 SQL 片段注入 params.dataScope
├── common/                            # BaseEntity / LoginUser / LoginUserHolder / Result
├── component/LoginUserLoader.java     # 加载当前登录用户
├── config/                            # LoginUserInterceptor + WebMvcConfig
├── controller/                        # UserController / DeptController / ContractController
├── entity/                            # SysUser / SysDept / SysUserDept / SysContract
├── mapper/                            # MyBatis Mapper 接口
├── service/                           # SysUserService(@DataScope) / SysDeptService / SysContractService(@DataScope)
src/main/resources/
├── application.yml                    # 数据源 + spring.sql.init + MyBatis 配置
├── schema.sql                         # 建表（幂等）
├── data.sql                           # 种子数据（部门树 + 用户 + 关联 + 合同）
└── mapper/                            # Mapper XML（${params.dataScope} 拼权限）
src/test/java/.../DataScopeTest.java   # 数据权限集成测试（用户 + 合同）
sql/init.sql                           # 一次性建库脚本
```

### 数据权限核心链路

```
请求头 X-User-Id: 3
   │
   ▼
LoginUserInterceptor ──加载用户→ LoginUserHolder(ThreadLocal)
   │
   ▼
UserController.list(SysUser) ──调用──▶ SysUserService.selectUserList(@DataScope)
   │                                     │
   │                                     ▼
   │                            DataScopeAspect(@Before)
   │                            │ 读 LoginUserHolder 取 data_scope=4
   │                            │ 拼出 d.dept_id IN (…FIND_IN_SET…)
   │                            │ 注入 query.params.dataScope
   │                                     │
   │                                     ▼
   │                            SysUserMapper.selectUserList
   │                                     │
   │                                     ▼
   │                            XML: WHERE … AND ${params.dataScope}
   ▼
返回当前用户权限内的数据
```

### 为什么不直接写死条件，而要注解 + 切面？

- 一个系统里"用户列表、订单列表、报表查询"等 N 个接口都要做数据权限，逻辑完全一样
- 用 `@DataScope` 一个注解标在方法上，权限逻辑集中在 `DataScopeAspect` 一处维护
- 切换权限级别（比如张三从"本部门"升为"本部门及以下"），只改用户配置，代码零改动

> 注意：`${params.dataScope}` 是字符串拼接（MyBatis 不会预编译），安全前提是片段**只由切面内部根据枚举值拼出**，绝不拼接任何外部输入，这也是若依(RuoYi)的经典做法。
