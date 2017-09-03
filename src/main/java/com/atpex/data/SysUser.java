package com.atpex.data;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by Atpex on 2017/8/7.
 */
@Data
public class SysUser {

    private final String id;
    private final String username;
    private final String password;
    private final String email;
    private final List<String> roles;
    private final Date lastPasswordResetDate;
}
