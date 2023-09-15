package com.challenge.viapath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ViapathApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViapathApplication.class, args);
	}

}
