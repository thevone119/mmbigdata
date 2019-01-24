package com.bingo;

import com.bingo.business.taobao.model.TBShop;
import com.bingo.business.taobao.service.TBShopService;
import com.bingo.common.exception.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * main方式启动
 */

@SpringBootApplication
@EnableAutoConfiguration
public class Mmbigdata2Application {
	public static void main(String[] args) {
		SpringApplication.run(Mmbigdata2Application.class, args);
		System.out.println("spring boot start!");
	}





}
