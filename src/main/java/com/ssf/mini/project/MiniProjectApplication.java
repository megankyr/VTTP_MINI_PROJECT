package com.ssf.mini.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniProjectApplication.class, args);

		String accessToken = System.getenv("ACCESS_TOKEN");
		System.out.println("ACCESS_TOKEN: " + accessToken);
	}
}
