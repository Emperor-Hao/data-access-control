package com.kele.dataaccesscontrol.mapper;

import com.kele.dataaccesscontrol.entity.SysContract;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysContractMapper {

    List<SysContract> selectContractList(SysContract query);
}
