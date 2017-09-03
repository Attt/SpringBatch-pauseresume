package com.atpex;

import com.atpex.schedule.StepExeListener;
import com.atpex.schedule.StepReadListener;
import com.atpex.schedule.StepSkipListener;
import com.atpex.service.DummyException;
import com.atpex.service.DummySkipPolicy;
import com.atpex.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@SpringBootApplication(scanBasePackages = "com.atpex")
@MapperScan("com.atpex.mapper")
@EnableBatchProcessing
@EnableScheduling
@Slf4j
public class StartApplication implements ApplicationContextAware {

    private StepExeListener stepExeListener;

    private RabbitTemplate rabbitTemplate;

    private volatile boolean bufferlock = false;

    private int DB_BATCH = 25000;

    private volatile BlockingQueue dbQueue;

    private volatile BlockingQueue mqQueue;

    private JobService jobService;


    public static void main(String[] args) {

        SpringApplication.run(StartApplication.class, args);
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void shutdown() {
        System.exit(0);
    }


    /**
     * 作业仓库
     *
     * @return
     * @throws Exception
     */
    @Bean("mapJry")
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager dataSourceTransactionManager) throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(dataSourceTransactionManager);
        jobRepositoryFactoryBean.setTablePrefix("atpex_");
        JobRepository jobRepository = jobRepositoryFactoryBean.getObject();
        this.jobService.setJobRepository(jobRepository);
        return jobRepository;
    }

    @Bean("stepReadListener")
    public StepReadListener stepReadListener() {
        return new StepReadListener();
    }

    @Bean("stepExeListener")
    public StepExeListener stepExeListener() {
        return new StepExeListener();
    }

    @Bean("stepSkipListener")
    public StepSkipListener stepSkipListener() {
        return new StepSkipListener();
    }

    @Bean("dummySkipPolicy")
    public DummySkipPolicy dummySkipPolicy() {
        return new DummySkipPolicy();
    }

    /**
     * 作业调度器
     *
     * @return
     * @throws Exception
     */
    @Bean("jl")
    public SimpleJobLauncher jobLauncher(@Qualifier("mapJry") JobRepository jobRepository) throws Exception {
//
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(new TaskExecutorAdapter(Executors.newFixedThreadPool(10)));
        jobLauncher.setJobRepository(jobRepository);
        this.jobService.setJobLauncher(jobLauncher);
        return jobLauncher;
    }

    @Bean("jobFact")
    public JobBuilderFactory jobBuilderFactory(@Qualifier("mapJry") JobRepository jobRepository) {
        return new JobBuilderFactory(jobRepository);
    }

    @Bean("stepFact")
    public StepBuilderFactory stepBuilderFactory(@Qualifier("mapJry") JobRepository jobRepository, PlatformTransactionManager dataSourceTransactionManager) {
        return new StepBuilderFactory(jobRepository, dataSourceTransactionManager);
    }

    @Bean("jobOperator")
    public JobOperator jobOperator(@Qualifier("mapJry") JobRepository jobRepository, @Qualifier("jobExplorer") JobExplorer jobExplorer, @Qualifier("jl") JobLauncher jobLauncher, @Qualifier("ejobRegistry") JobRegistry jobRegistry) {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.setJobRegistry(jobRegistry);
        simpleJobOperator.setJobRepository(jobRepository);
        this.jobService.setJobOperator(simpleJobOperator);
        return simpleJobOperator;
    }

    @Bean("jobExplorer")
    public JobExplorer jobExplorer(DataSource dataSource) throws Exception {
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(dataSource);
        jobExplorerFactoryBean.setTablePrefix("atpex_");
        jobExplorerFactoryBean.setJdbcOperations(new JdbcTemplate(dataSource));
        jobExplorerFactoryBean.setSerializer(new Jackson2ExecutionContextStringSerializer());
        JobExplorer jobExplorer = jobExplorerFactoryBean.getObject();
        this.jobService.setJobExplorer(jobExplorer);
        return jobExplorer;
    }

    @Bean("jobFactory")
    public ReferenceJobFactory referenceJobFactory(@Qualifier("importJb") Job job) {
        return new ReferenceJobFactory(job);
    }

    @Bean("ejobRegistry")
    public JobRegistry jobRegistry(@Qualifier("jobFactory") ReferenceJobFactory referenceJobFactory) throws DuplicateJobException {
        MapJobRegistry jobRegistry = new MapJobRegistry();
        jobRegistry.register(referenceJobFactory);
        return jobRegistry;
    }

    @Bean("jobRegistryBeanPostProcessor")
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(@Qualifier("ejobRegistry") JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean("importJb")
    public Job importJob(@Qualifier("jobFact") JobBuilderFactory jobs, Step step) {
        Job importJob =
                jobs.get("中文名称")
                        .incrementer(new RunIdIncrementer())
                        .flow(step)
                        .end()
                        .build();

        this.jobService.setJob(importJob);
        return importJob;
    }

    @Bean("personStep")
    public Step personStep(@Qualifier("stepFact") StepBuilderFactory stepBuilderFactory, @Qualifier("stepReadListener") ItemReadListener itemReadListener,
                           ItemReader<String> reader, ItemWriter<List<String>> writer, ItemProcessor<String, List<String>> processor,
                           StepSkipListener stepSkipListener, DummySkipPolicy dummySkipPolicy) {
        return stepBuilderFactory.get("personStep").listener(stepExeListener).listener(stepSkipListener).chunk(1)
                .reader(reader).listener(itemReadListener).readerIsTransactionalQueue()
                .processor(processor)
                .writer(writer).faultTolerant().skipPolicy(dummySkipPolicy).noRetry(DummyException.class).noRollback(DummyException.class).taskExecutor(new TaskExecutorAdapter(Executors.newFixedThreadPool(25))).throttleLimit(25)
                .build();
    }


    @Bean
    public ItemWriter<List<String>> prisonerItemWriter() {

        return list -> list.forEach(msgs -> msgs.forEach(msg -> {
            while (!dbQueue.offer(msg)) {
                log.info("dbQueue is FULL,re-offer");
            }
        }));
    }

    @Bean
    public ItemReader personItemReader() {
        return (ItemReader<String>) () -> {
            Message message = (Message) this.mqQueue.poll();
            if (message == null) {
                if (this.jobService.isStopped())
                    return null;
                TimeUnit.SECONDS.sleep(3);
                throw new DummyException();
            }
            return new String(message.getBody());
        };
    }


    @Bean
    public ItemProcessor<String, List<String>> personPrisonerItemProcessor() {
        return message -> {
            List<String> messages = new ArrayList<>();
            Random random = new Random();
            int size = random.nextInt(10);
            for (int i = 0; i < size; i++) {
                messages.add(message + i);
            }
            return messages;
        };
    }


    @Bean("dbQueue")
    public BlockingQueue dbQueue() {
        return new ArrayBlockingQueue(160000);
    }

    @Bean("mqQueue")
    public BlockingQueue mqQueue() {
        return new ArrayBlockingQueue(16000);
    }


    @Scheduled(fixedRate = 2 * 1000)
    public void mqWatcher() {
        if (!bufferlock) {
            bufferlock = true;
            boolean isEmpty = true;
            try {
                for (; ; ) {
                    Message message = this.rabbitTemplate.receive("ME-Q");
                    if (message == null) {
                        break;
                    }
                    isEmpty = false;
                    while (!mqQueue.offer(message)) {
                        log.info("mqQueue is FULL,re-offer");
                    }
                }
            } finally {
                bufferlock = false;
            }
            if (!isEmpty) {
                jobService.keepJobAlive();
            }
        }
    }

    @Scheduled(fixedRate = 20 * 1000)
    public void jobDaemon() throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        jobService.tryToStopTheJob();
    }

    @Scheduled(fixedRate = 500)
    public void mqQueueWatcher() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, NoSuchJobException {
        if (mqQueue.size() != 0) {
            this.jobService.startOrResumeJob();
        }
    }

    @Scheduled(fixedRate = 5 * 1000)
    public void dbWatcher() {
        int count = 0;

        for (int i = 0; i <= DB_BATCH; i++) {
            count = i;
            String message = (String) this.dbQueue.poll();
            if (message == null)
                break;
        }
        if (count != 0)
            log.info("write {} data to DB", count);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        stepExeListener = applicationContext.getBean("stepExeListener", StepExeListener.class);
        this.rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        this.dbQueue = applicationContext.getBean("dbQueue", BlockingQueue.class);
        this.mqQueue = applicationContext.getBean("mqQueue", BlockingQueue.class);
        jobService = applicationContext.getBean(JobService.class);
    }
}
