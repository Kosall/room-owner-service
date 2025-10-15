package com.piseth.java.school.roomservice.config;

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
		UrlBasedCorsConfigurationSource sourcing=new UrlBasedCorsConfigurationSource();
		sourcing.registerCorsConfiguration("/api/**", configuration);
		return new CorsWebFilter(sourcing);
	}
}
