package com.atpex.service;

import com.atpex.data.SysUser;

/**
 *
 * Created by Atpex on 2017/6/3.
 */
public interface UserRolePermService {
    SysUser findByUserName(String username);
}
