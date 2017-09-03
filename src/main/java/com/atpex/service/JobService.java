package com.atpex.service;

import com.atpex.mapper.UserRoleMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Atpex on 2017/9/2.
 */
@Service
@Slf4j
@Data
public class JobService implements ApplicationListener {

    private final UserRoleMapper userRoleMapper;

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private Job job;

    private Long jobExecutionId = -1L;

//    private JobExecution jobExecution;

    private volatile long lastVersion = -1;

    private volatile long runningVersion = 0;

    private volatile boolean isStopped;

    private SimpleJobOperator jobOperator;

    private JobExplorer jobExplorer;


    @Autowired
    public JobService(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }

    void shutJob() {
        log.info("shutJob shut!!!!!!!!!!!!");
        JobExecution jobExecution = getJobExecution(this.jobExecutionId);
        if (jobExecution != null) {
            this.userRoleMapper.shutJob(jobExecution.getJobId());
            this.userRoleMapper.shutStep(jobExecution.getJobId());
        }
        log.info("shutJob shut-------------");
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            shutJob();
        }
    }

    public synchronized void tryToStopTheJob() throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        JobExecution jobExecution = getJobExecution(this.jobExecutionId);
        if (lastVersion != runningVersion) {
            lastVersion = runningVersion;
            return;
        }
        if (jobExecution != null && !jobExecution.isStopping() && jobExecution.getExitStatus().isRunning()) {
            log.info("stop the job.");
            this.jobOperator.stop(jobExecution.getId());
            isStopped = true;
        }
    }

    public void keepJobAlive() {
        log.debug("keep alive {}", runningVersion + 1);
        runningVersion++;
    }

    public synchronized void startOrResumeJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException {
        JobExecution jobExecution = getJobExecution(this.jobExecutionId);
        if (jobExecution == null) {
            log.info("start the job.");
            if (JobParameterHolder.jobParameters == null) {
                Map<String, JobParameter> parameters = new HashMap<>();
                parameters.put("CURR_TIME", new JobParameter(System.currentTimeMillis()));
                JobParameterHolder.jobParameters = new JobParameters(parameters);
            }
            runningVersion = 0;
            lastVersion = -1;
            this.jobExecutionId = this.jobLauncher.run(this.job, JobParameterHolder.jobParameters).getId();
        } else if (!jobExecution.getExitStatus().isRunning()) {
            log.info("resume the job.");
            runningVersion = 0;
            lastVersion = -1;
            this.jobExecutionId = this.jobOperator.restart(jobExecution.getId());
            isStopped = false;
        }
    }

    private JobExecution getJobExecution(Long executionId) {
        JobExecution jobExecution = this.jobExplorer.getJobExecution(executionId);
        if(jobExecution!=null)
            log.debug("jobExecution:{},{}", jobExecution.getStatus(), jobExecution.getExitStatus());
        return jobExecution;
    }
}
