package com.gbst.complii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource("classpath:beans-complii.xml")
public class GbstCompliiIntegrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(GbstCompliiIntegrationApplication.class, args);
	}
}
