package com.sope.configuration;

import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sope.controller.ApiStructure;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"com.sope.controller"})
public class WebApplicationConfiguration extends WebMvcConfigurerAdapter {

	 @Override
	 public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("**" + ApiStructure.VERSION + "/api/**")
        .allowedOrigins("*")
	    .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE")
	    .allowedHeaders("X-Auth-Token")
	    .allowCredentials(true)
	    .maxAge(3600);
	 }

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder()
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		converters.add(new MappingJackson2HttpMessageConverter(jackson2ObjectMapperBuilder.build()));
		converters.add(new StringHttpMessageConverter());
	}
}
