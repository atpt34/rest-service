package com.oleksa.snapshot;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class SnapshotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnapshotApplication.class, args);
	}

	@Bean
			@Lazy
//	@Order(value = 5000)
	CommandLineRunner cmd1() {
		return args -> {
			System.out.println("cmd 1");
		};
	}

	@Bean
//	@Order(value = 50000)
	ApplicationRunner cmd2() {
		return args -> {
			System.out.println("cmd 2");
//			HttpClient httpClient = HttpClient.newHttpClient();
//			httpClient.send()
		};
	}

//	@Bean
//	public WebSecurityConfigurerAdapter webSecurity() {
//		return new WebSecurityConfigurerAdapter() {
//			@Override
//			protected void configure(HttpSecurity httpSecurity) throws Exception {
//				httpSecurity.antMatcher("/**")
//                        .authorizeRequests()
//                        .anyRequest()
//                        .permitAll();
//			}
//		};
//	}
}
