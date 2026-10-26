package com.kele.dataaccesscontrol.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解。
 * <p>标注在需要做数据权限过滤的查询方法上，由 {@code DataScopeAspect} 在方法执行前
 * 根据当前登录用户的数据权限拼出 SQL 片段，注入到参数实体的 params.dataScope 中，
 * 最终通过 XML 中的 {@code ${params.dataScope}} 拼入 WHERE 条件。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /** 部门表在查询 SQL 中的别名，用于拼接部门权限片段 */
    String deptAlias() default "d";

    /** 用户表在查询 SQL 中的别名，用于拼接"仅本人"权限片段 */
    String userAlias() default "u";
}
