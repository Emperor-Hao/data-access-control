package com.kele.dataaccesscontrol.mapper;

import com.kele.dataaccesscontrol.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDeptMapper {

    List<SysDept> selectDeptList(SysDept query);
}
