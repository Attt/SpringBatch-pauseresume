package com.atpex.config.security;

import com.atpex.data.SysUser;
import com.atpex.service.UserRolePermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * Created by Atpex on 2017/8/7.
 */
@Service
public class JWTUserDetailsService implements UserDetailsService {


    private final UserRolePermService userRolePermService;

    @Autowired
    public JWTUserDetailsService(UserRolePermService userRolePermService) {
        this.userRolePermService = userRolePermService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = this.userRolePermService.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return JWTUserFactory.create(user);
        }
    }
}
