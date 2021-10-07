package org.hung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Mqtt5LoadtestClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(Mqtt5LoadtestClientApplication.class, args);
	}

}
