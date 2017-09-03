package com.atpex.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import java.util.List;

/**
 *
 * Created by Atpex on 2017/8/12.
 */
public class MongoJobInstanceDao implements JobInstanceDao, InitializingBean {

    private MongoRepository mongoRepository;

    private DataFieldMaxValueIncrementer jobIncrementer;

    private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();

    public MongoJobInstanceDao(MongoRepository mongoRepository){
        this.mongoRepository = mongoRepository;
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
        return null;
    }

    @Override
    public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
        return null;
    }

    @Override
    public JobInstance getJobInstance(Long instanceId) {
        return null;
    }

    @Override
    public JobInstance getJobInstance(JobExecution jobExecution) {
        return null;
    }

    @Override
    public List<JobInstance> getJobInstances(String jobName, int start, int count) {
        return null;
    }

    @Override
    public List<String> getJobNames() {
        return null;
    }

    @Override
    public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
        return null;
    }

    @Override
    public int getJobInstanceCount(String jobName) throws NoSuchJobException {
        return 0;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }


    /**
     * Setter for {@link DataFieldMaxValueIncrementer} to be used when
     * generating primary keys for {@link JobInstance} instances.
     *
     * @param jobIncrementer
     *            the {@link DataFieldMaxValueIncrementer}
     */
    public void setJobIncrementer(DataFieldMaxValueIncrementer jobIncrementer) {
        this.jobIncrementer = jobIncrementer;
    }
}
