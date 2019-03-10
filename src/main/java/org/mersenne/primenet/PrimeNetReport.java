package org.mersenne.primenet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(PrimeNetProperties.class)
@SpringBootApplication
public class PrimeNetReport {

	public static void main(String[] args) {
		SpringApplication.run(PrimeNetReport.class, args);
	}

}

