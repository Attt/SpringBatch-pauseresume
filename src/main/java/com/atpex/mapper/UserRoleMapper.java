package com.atpex.mapper;

import com.atpex.model.UserRole;

import java.util.List;

public interface UserRoleMapper {

    List<UserRole> selectByUser(Long uid);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    int shutJob(long jobExecutionId);

    int shutStep(long jobExecutionId);
}