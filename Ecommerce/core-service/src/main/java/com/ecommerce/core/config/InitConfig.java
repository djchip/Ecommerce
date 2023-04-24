package com.ecommerce.core.config;

import com.ecommerce.core.util.TokenJWTUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class InitConfig {

    @Bean(initMethod = "init")
    public InitBean initBean() {
        return new InitBean();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    @Bean
    public TokenJWTUtils tokenJWTUtils() {
        return new TokenJWTUtils();
    }

}
