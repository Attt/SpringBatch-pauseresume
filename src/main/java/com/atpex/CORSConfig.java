package com.atpex;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by Atpex on 2017/7/10.
 */
@Configuration
public class CORSConfig implements WebMvcConfigurer {

//    private final HandlerInterceptor interceptor;
//
//    @Autowired
//    public CORSConfig(HandlerInterceptor interceptor) {
//        this.interceptor = interceptor;
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(interceptor);
//    }

    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("1","2");
        List<String> list = new ArrayList<>();

        list.add("xx");
        list.add("yy");

        list.forEach(System.out::println);

        map.forEach((k,v)->{
            System.out.println(k);
            System.out.println(v);
        });
    }
}
