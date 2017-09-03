package com.atpex;

import com.atpex.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Atpex on 2017/9/3.
 */
//@Component
//@EnableScheduling
//@EnableAutoConfiguration
@Slf4j
public class Config  implements ApplicationContextAware {
    private RedisTemplate redisTemplate;

    private RabbitTemplate rabbitTemplate;

    private final static String SEND_RATE_SIGNAL = "SEND_RATE_SIGNAL";

    private volatile boolean bufferlock = false;

    private volatile boolean joblock = false;

    private volatile boolean sendlock = false;

    private int DB_BATCH = 25000;

    private String DB_SEND_BATCH_HIGHEST_RATE = "4000";

    private String DB_SEND_BATCH_MIDDLE_RATE = "2000";

    private String DB_SEND_BATCH_LOWER_RATE = "1000";

    private String DB_SEND_BATCH_LOWEST_RATE = "0";

    private JobService jobService;

    private BlockingQueue mqQueue;

    private BlockingQueue dbQueue;

    private volatile int nowSendRate;

    private volatile int total = 0;


    @Bean("dbQueue")
    public BlockingQueue dbQueue(){
        return new ArrayBlockingQueue(160000);
    }

    @Bean("mqQueue")
    public BlockingQueue mqQueue(){
        return new ArrayBlockingQueue(16000);
    }


//    @Scheduled(fixedRate = 2 * 1000)
    public void mqWatcher(){
        if(!bufferlock) {
            bufferlock = true;
            try {
                for(int i = 0;i < 8000;i++) {
                    Message message = this.rabbitTemplate.receive("ME-Q");
                    if(message==null)
                        break;
                    while(!mqQueue.offer(message)){
                        log.info("mqQueue is FULL,re-offer");
                    }
                }
            }finally {
                bufferlock = false;
            }
        }
    }

//    @Scheduled(fixedRate = 1000)
    public void mqQueueWatcher() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        if(mqQueue.size()!=0){
//            this.jobService.startOrResumeJob();
        }
    }

//    @Scheduled(fixedRate = 5 * 1000)
    public void dbWatcher(){
        int count = 0;

        for(int i =0;i<=DB_BATCH;i++){
            count = i;
            Message message = (Message) this.dbQueue.poll();
            if(message==null)
                break;
        }
        log.info("write {} data to DB",count);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.redisTemplate = applicationContext.getBean("stringRedisTemplate",RedisTemplate.class);
//        this.redisTemplate.opsForValue().set(SEND_RATE_SIGNAL, DB_SEND_BATCH_HIGHEST_RATE);
        this.rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        this.dbQueue = applicationContext.getBean("dbQueue",BlockingQueue.class);
        this.mqQueue = applicationContext.getBean("mqQueue",BlockingQueue.class);
        this.jobService = applicationContext.getBean(JobService.class);
    }

    public void unlock(){
        this.joblock = false;
    }
}
