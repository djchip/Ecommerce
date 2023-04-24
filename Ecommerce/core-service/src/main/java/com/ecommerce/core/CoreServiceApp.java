package com.ecommerce.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Neo Team
 * @Email: @neo.vn
 * @Version 1.0.0
 */

@SpringBootApplication
@EnableEurekaClient
public class CoreServiceApp {
	public static void main(String[] args) {
		SpringApplication.run(CoreServiceApp.class, args);
	}
}
