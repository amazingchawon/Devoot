package com.gamee.devoot_backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@Profile("!test")
public class EsClientConfig extends ElasticsearchConfiguration {
	@Value("${ELASTIC_PASSWORD}")
	private String password;

	@Bean
	@Override
	public ClientConfiguration clientConfiguration() {
		return ClientConfiguration.builder()
			.connectedTo("devoot-elasticsearch:9200")
			.withBasicAuth("elastic", password)
			.build();
	}
}
