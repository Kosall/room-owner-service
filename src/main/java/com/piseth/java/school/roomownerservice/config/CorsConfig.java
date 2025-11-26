package com.piseth.java.school.roomownerservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
	@Bean
	CorsWebFilter corsWebFilter() {
		CorsConfiguration configuration=new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:4200/");
		configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTION"));
		configuration.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource sourcing=new UrlBasedCorsConfigurationSource();
		sourcing.registerCorsConfiguration("/api/**", configuration);
		return new CorsWebFilter(sourcing);
	}
}
