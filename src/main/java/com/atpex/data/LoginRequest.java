package com.atpex.data;

import lombok.Data;
import lombok.ToString;

/**
 * 登录信息
 * Created by Atpex on 2017/7/17.
 */
@Data
@ToString
public class LoginRequest {
    private String email;

    private String password;

}
