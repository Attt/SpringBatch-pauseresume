package com.atpex.config.wrapper;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

/**
 *
 * Created by Atpex on 2017/8/7.
 */
@RestController
public class TokenErrorController extends BasicErrorController {

    public TokenErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    private static final String PATH = "/error";

    @RequestMapping(produces = "text/html")
    @Override
    public ModelAndView errorHtml(HttpServletRequest request,
                                  HttpServletResponse response) {
        System.out.println(22222);
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
                request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView == null ? new ModelAndView("error", model) : modelAndView);
    }

    @RequestMapping
    @ResponseBody
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        System.out.println(11111);
        Map<String, Object> body = getErrorAttributes(request,
                isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(body, status);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
