package com.bdt.Sparkstream.service;

import com.bdt.Sparkstream.config.KafkaConf;
import com.bdt.Sparkstream.util.Util;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class SparkService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final SparkConf sparkConf;
    private final KafkaConf kafkaConf;
    private final Collection<String> topics;

    @Autowired
    public SparkService(SparkConf sparkConf,
                                KafkaConf kafkaConf,
                                @Value("${spring.kafka.template.default-topic}") String[] topics) {
        this.sparkConf = sparkConf;
        this.kafkaConf = kafkaConf;
        this.topics = Arrays.asList(topics);
    }

    public void start(){
        log.debug("Running Sparkstream service..");


        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(10));


        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaConf.consumerConfigs()));


        JavaDStream<String> lines = messages.map(stringStringConsumerRecord -> stringStringConsumerRecord.value());


        lines
                .count()
                .map(cnt -> "Received hashtags : " + cnt + " total tweets):")
                .print();


        lines
                .flatMap(text -> Util.hashTagsFromTweet(text))
                .mapToPair(hashTag -> new Tuple2<>(hashTag, 1))
                .reduceByKey((a, b) -> Integer.sum(a, b))

                .mapToPair(stringIntegerTuple2 -> stringIntegerTuple2.swap())
                .foreachRDD(rrdd -> {
                    System.out.println("***********************************************************************************");
                    List<Tuple2<Integer, String>> sorted;
                    JavaPairRDD<Integer, String> counts = rrdd.sortByKey(false);
                    sorted = counts.collect();
                    sorted.forEach( record -> {
                        System.out.println(String.format(" %s [%d]", record._2, record._1));
                    });
                });

        // Start the computation
        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
