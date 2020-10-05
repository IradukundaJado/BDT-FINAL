package com.bdt.twitterKafka.service;

import com.bdt.twitterKafka.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class TweetEventService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String kafkaTopic;
    private final Twitter twitter;
    private final KafkaProducer kafkaProducer;

    public TweetEventService (Twitter twitter,
                                    KafkaProducer kafkaProducer,
                                    @Value(value = "${spring.kafka.template.default-topic}") String kafkaTopic){
        this.twitter = twitter;
        this.kafkaProducer = kafkaProducer;
        this.kafkaTopic = kafkaTopic;
    }

    public void start() {
        List<StreamListener> listeners = new ArrayList<StreamListener>();

        StreamListener streamListener = new StreamListener() {

            @Override
            public void onTweet(Tweet tweet) {
                String lang = tweet.getLanguageCode();
                String text = tweet.getText();


                if (!"en".equals(lang)) {
                    return;
                }

                Iterator<String> hashTags = Util.hashTagsFromTweet(text);


                if (!hashTags.hasNext()) {
                    return;
                }

                log.info("User '{}', Tweeted : {}, from ; {}", tweet.getUser().getName() , tweet.getText(), tweet.getUser().getLocation());
                kafkaProducer.send(kafkaTopic, tweet.getText());
            }

            @Override
            public void onDelete(StreamDeleteEvent deleteEvent) {
                log.debug("onDelete");
            }

            @Override
            public void onLimit(int numberOfLimitedTweets) {
                log.debug("onLimit");
            }

            @Override
            public void onWarning(StreamWarningEvent warningEvent) {
                log.debug("onLimit");
            }

        };

        //Start Stream when run a service
        listeners.add(streamListener);
        twitter.streamingOperations().sample(listeners);
    }
}
