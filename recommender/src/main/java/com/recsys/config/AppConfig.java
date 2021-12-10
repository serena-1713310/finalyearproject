package com.recsys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.recsys.forms.RecommendFormData;

@Configuration
public class AppConfig {

	@Bean
	public RecommendFormData recommendFormData() {
		return new RecommendFormData();
	}
	
}
