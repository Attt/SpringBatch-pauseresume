package com.atpex.job;

import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.AbstractJobRepositoryFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * Created by Atpex on 2017/8/12.
 */
public class MongoJobRepositoryFactoryBean extends AbstractJobRepositoryFactoryBean implements InitializingBean {

    private MongoRepository mongoRepository;

    @Override
    protected JobInstanceDao createJobInstanceDao() throws Exception {

        return new MongoJobInstanceDao(this.mongoRepository);
    }

    @Override
    protected JobExecutionDao createJobExecutionDao() throws Exception {
        return null;
    }

    @Override
    protected StepExecutionDao createStepExecutionDao() throws Exception {
        return null;
    }

    @Override
    protected ExecutionContextDao createExecutionContextDao() throws Exception {
        return null;
    }

    public void setMongoRepository(MongoRepository mongoRepository){
        this.mongoRepository = mongoRepository;
    }
}
