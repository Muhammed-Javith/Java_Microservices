package com.mj.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain userDefinedFilter(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(request -> request.requestMatchers("/api/employeess/").permitAll()
				.anyRequest().authenticated()).formLogin();
		return http.build();
	}

}
