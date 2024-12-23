package ru.skillbox.mc_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class McAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(McAccountApplication.class, args);
	}

}
