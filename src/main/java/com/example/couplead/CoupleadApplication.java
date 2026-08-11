package com.example.couplead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CoupleadApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoupleadApplication.class, args);
	}

}
