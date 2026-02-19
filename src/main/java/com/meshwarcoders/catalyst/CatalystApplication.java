package com.meshwarcoders.catalyst;

import com.meshwarcoders.catalyst.api.security.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CatalystApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalystApplication.class, args);
		System.out.println("App Started!!!!!!!!!");
	}

}
