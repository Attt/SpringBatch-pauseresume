package com.atpex.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Atpex on 2017/6/28.
 */
//@Component
//@EnableScheduling
public class DynamicScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicScheduler.class);

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private ScheduledFuture<?> future;

    private volatile static String cron;


    public DynamicScheduler() {
        cron = "0 0/5 * * * ?";
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void rewind(){
        boolean modified = false;
        try(RandomAccessFile aFile = new RandomAccessFile("H:\\test.txt", "rw");
            FileChannel fileChannel = aFile.getChannel()) {
            StringBuilder stringBuilder = new StringBuilder();
            ByteBuffer buf = ByteBuffer.allocate(48);
            while(fileChannel.read(buf)!= -1){
                buf.flip();
                stringBuilder.append(new String(buf.array()));
                buf.clear();
            }
            modified = !cron.equals(stringBuilder.toString());
            cron = stringBuilder.toString();
        } catch (IOException e){
            LOGGER.error(e.getMessage());
        }finally {
            if(modified) {
                LOGGER.info("cron is modified,now cron is {}",cron);
                stopCronTask();
                startCronTask();
            }
        }
    }


    private void startCronTask(){
        CronTrigger cronTrigger = new CronTrigger(cron);
        future = threadPoolTaskScheduler.schedule(this::doSomething,cronTrigger);
        LOGGER.info("start crontask at {}",cron);
    }

    private void stopCronTask(){
        if (future != null) {
            future.cancel(true);
            LOGGER.info("stop crontask at {}",cron);
        }
    }

    private void doSomething(){
        LOGGER.info("I'm doing something.");
    }
}
