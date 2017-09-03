package com.atpex.service;

import org.springframework.batch.core.JobParameters;
import org.springframework.stereotype.Component;

/**
 * Created by Atpex on 2017/9/3.
 */
@Component
public class JobParameterHolder {

    public volatile static JobParameters jobParameters;

}
