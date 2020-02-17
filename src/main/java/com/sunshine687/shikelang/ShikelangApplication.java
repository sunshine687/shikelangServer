package com.sunshine687.shikelang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="com.sunshine687")
public class ShikelangApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		//Application的类名
		return application.sources(ShikelangApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(ShikelangApplication.class, args);
	}

}

