package com.atpex.schedule;

import com.hankcs.hanlp.dictionary.BaseSearcher;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 *
 * Created by Atpex on 2017/8/9.
 */
@Component
@EnableScheduling
public class ScheduledJob implements ApplicationContextAware{

    private final JobLauncher jobLauncher;
//
    private final Job job;

    private volatile boolean flag = false;

    private JobParameters jobParameters;


    @Autowired
    public ScheduledJob(@Qualifier("jl") JobLauncher jobLauncher,@Qualifier("importJb") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

//    @Scheduled(fixedRate = 20000)
//    public void run() throws Exception {
////        Map<String, JobParameter> parameters = new HashMap<>();
////        parameters.put("CURR_TIME", new JobParameter(System.currentTimeMillis()));
////        jobLauncher.run(job, new JobParameters(parameters));
//        if(!flag) {
//            flag = true;
//            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//            jobExecution.stop();
//        }
//    }


    public static void main(String[] args) {
        // 动态增加
//        CustomDictionary.remove("攻城");
        CustomDictionary.add("保险");
        // 强行插入
//        CustomDictionary.insert("白富美", "nz 1024");
        // 删除词语（注释掉试试）
//        CustomDictionary.remove("攻城狮");
//        System.out.println(CustomDictionary.add("单身狗", "nz 1024 n 1"));
//        System.out.println(CustomDictionary.get("单身狗"));

        String text = "攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，走上人生巔峰攻城獅逆袭单身狗，迎娶白富美，保险";  // 怎么可能噗哈哈！

        // AhoCorasickDoubleArrayTrie自动机分词
//        final char[] charArray = text.toCharArray();
//        CustomDictionary.parseText(charArray, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>()
//        {
//            @Override
//            public void hit(int begin, int end, CoreDictionary.Attribute value)
//            {
//                System.out.printf("[%d:%d]=%s %s\n", begin, end, new String(charArray, begin, end - begin), value);
//            }
//        });
//        text = HanLP.convertToSimplifiedChinese(text);
//        System.out.println(text);
//
//        for(Pinyin p:HanLP.convertToPinyinList(text))
//            System.out.println(p);
        // trie树分词
        System.out.println(System.currentTimeMillis());
        for(int i =0;i<1000000;i++) {
            BaseSearcher searcher = CustomDictionary.getSearcher(text);
            Map.Entry entry;
            while ((entry = searcher.next()) != null) {
                System.out.println(entry);
            }
        }
        System.out.println(System.currentTimeMillis());
        // 标准分词
//        System.out.println(HanLP.segment(text));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map map = applicationContext.getBeansOfType(DataSource.class);
//        jobParameters = applicationContext.getBean("jobParameters",JobParameters.class);
    }
}
