package com.atpex.mapper;

import com.atpex.model.RolePermission;

import java.util.List;

public interface RolePermissionMapper {

    List<RolePermission> selectByRole(Long rid);

    int insert(RolePermission record);

    int insertSelective(RolePermission record);
}