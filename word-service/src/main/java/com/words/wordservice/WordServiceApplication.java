package com.words.wordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.words")
public class WordServiceApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(WordServiceApplication.class, args);
	}
}

