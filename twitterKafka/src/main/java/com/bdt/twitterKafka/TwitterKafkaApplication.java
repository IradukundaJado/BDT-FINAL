package com.bdt.twitterKafka;

import com.bdt.twitterKafka.service.TweetEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwitterKafkaApplication implements CommandLineRunner {

	@Autowired
	private TweetEventService tweetEventService;

	public static void main(String[] args) {
		SpringApplication.run(TwitterKafkaApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		tweetEventService.start();
	}
}
