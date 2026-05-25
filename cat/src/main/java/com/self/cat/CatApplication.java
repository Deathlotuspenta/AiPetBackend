package com.self.cat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // This turns on async support. (这会开启异步支持。)
@MapperScan(value = "com.self.cat.**.mapper")
public class CatApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatApplication.class, args);
	}

}
