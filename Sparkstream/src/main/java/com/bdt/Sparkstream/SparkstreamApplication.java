package com.bdt.Sparkstream;

import com.bdt.Sparkstream.service.SparkService;
import com.bdt.Sparkstream.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SparkstreamApplication implements CommandLineRunner {

	@Autowired
	private SparkService sparkService;


	private Util util = new Util();

	public static void main(String[] args) {
		SpringApplication.run(SparkstreamApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception    {
		sparkService.start();
	}
}
