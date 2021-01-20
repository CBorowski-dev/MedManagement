package de.medmanagement;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class MedManagementApplication {

	public static void main(String[] args) {
		System.setProperty("server.servlet.context-path", "/medmanagement");
		SpringApplication.run(MedManagementApplication.class, args);
	}

}
