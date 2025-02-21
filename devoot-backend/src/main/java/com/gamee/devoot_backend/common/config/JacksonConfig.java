package com.gamee.devoot_backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		return Jackson2ObjectMapperBuilder.json().indentOutput(true);
	}
}
