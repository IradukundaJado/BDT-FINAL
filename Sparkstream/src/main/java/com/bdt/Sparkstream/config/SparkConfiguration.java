package com.bdt.Sparkstream.config;

import org.apache.spark.SparkConf;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SparkConfiguration {

    @Bean
    public SparkConf sparkConf() {
        return new org.apache.spark.SparkConf()
                .setAppName("JavaDirectKafkaWordCount")
                .setMaster("local[2]")
                .set("spark.executor.memory","1g");
    }
}
