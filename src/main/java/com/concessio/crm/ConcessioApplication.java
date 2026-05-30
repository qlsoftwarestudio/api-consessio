package com.concessio.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(excludeName = {
    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration"
})
public class ConcessioApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcessioApplication.class, args);
	}

}
