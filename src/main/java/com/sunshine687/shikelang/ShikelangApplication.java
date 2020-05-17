package com.sunshine687.shikelang;

import com.sunshine687.shikelang.util.QuartzManager;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(value = "com.sunshine687.shikelang")
//开启基于注解的定时任务
@EnableScheduling
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

