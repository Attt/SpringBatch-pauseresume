package com.atpex.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Atpex on 2017/9/2.
 */
@Component
public class ShutDown extends Thread{

    private final JobService jobService;

    @Autowired
    public ShutDown(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void run() {
        System.out.println("shut!!!!!!!!");
        jobService.shutJob();
        System.out.println("shut----------");
    }


}
