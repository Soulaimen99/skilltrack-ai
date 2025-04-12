package com.skilltrack.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties( prefix = "openai" )
@Data
public class OpenAiProperties {

	private String model;
	private double temperature;
	private int maxTokens;
}
