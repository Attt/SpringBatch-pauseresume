package com.atpex.service.iml;

import com.atpex.data.SysUser;
import com.atpex.mapper.*;
import com.atpex.model.User;
import com.atpex.service.UserRolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * Created by Atpex on 2017/6/3.
 */
@Service
public class UserRolePermServiceImpl implements UserRolePermService {

    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;


    @Autowired
    public UserRolePermServiceImpl(PermissionMapper permissionMapper, RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper, UserMapper userMapper, UserRoleMapper userRoleMapper) {
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public SysUser findByUserName(String username){
        User condition = new User();
        condition.setNickname(username);
        User user = this.userMapper.selectBySelective(condition);
        return new SysUser(String.valueOf(user.getId()),user.getNickname(),user.getPswd(),user.getEmail(),new ArrayList<>(),new Date());
    }
}
