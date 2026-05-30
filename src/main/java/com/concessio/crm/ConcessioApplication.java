package com.concessio.crm;

import com.concessio.crm.config.RailwayEnvironmentListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConcessioApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ConcessioApplication.class);
		app.addListeners(new RailwayEnvironmentListener());
		app.run(args);
	}

}
