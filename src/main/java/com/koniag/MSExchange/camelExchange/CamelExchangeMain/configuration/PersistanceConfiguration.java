package com.koniag.MSExchange.camelExchange.CamelExchangeMain.configuration;

import org.elasticsearch.node.NodeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.koniag.MSExchange.camelExchange.CamelExchangeMain.repositories")
public class PersistanceConfiguration {

	@Bean
	public NodeBuilder nodeBuilder() {
		return new NodeBuilder();
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() {
		return new ElasticsearchTemplate(nodeBuilder().local(true).node().client());
	}
}
