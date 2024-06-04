package com.example.apartmenttradedata;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class ApartmentTradeDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApartmentTradeDataApplication.class, args);
	}

}
