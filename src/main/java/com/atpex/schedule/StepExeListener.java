package com.atpex.schedule;

import com.atpex.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Atpex on 2017/9/3.
 */
@Slf4j
public class StepExeListener implements StepExecutionListener,ApplicationContextAware {

    private BlockingQueue mqQueue;

    private JobService jobService;

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
//        if(this.mqQueue.size()==0){
//            log.info("sss");
//            this.jobService.stopJob();
//        }
        return stepExecution.getExitStatus();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.mqQueue = applicationContext.getBean("mqQueue",BlockingQueue.class);
        this.jobService = applicationContext.getBean(JobService.class);
    }
}
