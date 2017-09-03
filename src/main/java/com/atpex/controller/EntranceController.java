package com.atpex.controller;

import com.atpex.config.wrapper.ResponseWrapper;
import com.atpex.data.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * FIXME 日志分离
 * Created by Atpex on 2017/6/3.
 */

@RestController
@Slf4j
public class EntranceController {
//    private static final Logger logger = LoggerFactory.getLogger(EntranceController.class);


    @RequestMapping("/mustDoLogin")
    public Object mustDoLogin() {
        log.info("mustDoLogin!!");
        return ResponseWrapper.of("mustDoLogin");
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody LoginRequest request) {
        log.info("Login SUCCESS.");
        return ResponseWrapper.of(SecurityUtils.getSubject().getPrincipal());
    }

    @RequestMapping("/loginSuccess")
    public Object loginSuccess() {
        log.info("loginSuccess!!");
        return ResponseWrapper.of("loginSuccess");
    }

    @RequestMapping("/logout")
    public Object logout() {
        SecurityUtils.getSubject().logout();
        log.info("logout!!");
        return ResponseWrapper.of(null);
    }


    @RequestMapping("/unAuthorized")
    public Object unAuthorized() {
        log.info("unAuthorized");
        return ResponseWrapper.of("unAuthorized");
    }

    @RequestMapping("/action1")
    public Object action1() {
        log.info("action1");
        return ResponseWrapper.of("action1");
    }

    @RequestMapping("/action2")
    public Object action2() {
        log.info("action2");
        return ResponseWrapper.of("action2");
    }

}
