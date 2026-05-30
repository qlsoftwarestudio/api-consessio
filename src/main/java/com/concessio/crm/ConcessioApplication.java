package com.concessio.crm;

import java.net.URI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConcessioApplication {

	public static void main(String[] args) {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl == null || databaseUrl.isBlank()) {
			databaseUrl = System.getenv("SPRING_DATASOURCE_URL");
		}
		if (databaseUrl == null || databaseUrl.isBlank()) {
			databaseUrl = System.getProperty("spring.datasource.url");
		}

		if (databaseUrl != null && !databaseUrl.isBlank() && databaseUrl.startsWith("postgresql://")) {
			try {
				URI uri = URI.create(databaseUrl);
				String userInfo = uri.getUserInfo();
				String host = uri.getHost();
				int port = uri.getPort();
				String path = uri.getPath();

				if (path != null && path.startsWith("/")) {
					path = path.substring(1);
				}

				String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, path);
				System.setProperty("spring.datasource.url", jdbcUrl);

				if (userInfo != null && userInfo.contains(":")) {
					String[] parts = userInfo.split(":", 2);
					System.setProperty("spring.datasource.username", parts[0]);
					System.setProperty("spring.datasource.password", parts[1]);
				}

				System.out.println("Railway DATABASE_URL parsed. JDBC URL: " + jdbcUrl);
			} catch (Exception e) {
				System.err.println("Failed to parse DATABASE_URL/SPRING_DATASOURCE_URL: " + e.getMessage());
			}
		} else if (databaseUrl != null) {
			System.out.println("Using existing datasource URL: " + databaseUrl);
		}

		SpringApplication.run(ConcessioApplication.class, args);
	}

}
