package com.atpex.schedule;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by Atpex on 2017/7/1.
 */
@Component
public class CoolTest implements ApplicationContextAware {

    private Map<String, Object> map;

    @PostConstruct
    public void test() {
        System.out.println("aaaa");
        if(map.size()!=0){
            for(String k:map.keySet()){
                Object o = map.get(k);

            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        map = applicationContext.getBeansWithAnnotation(Retry.class);
    }
}
