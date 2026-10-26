package com.kele.dataaccesscontrol.mapper;

import com.kele.dataaccesscontrol.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper {

    List<SysUser> selectUserList(SysUser query);

    SysUser selectUserById(Long userId);

    List<Long> selectDeptIdsByUserId(@Param("userId") Long userId);
}
