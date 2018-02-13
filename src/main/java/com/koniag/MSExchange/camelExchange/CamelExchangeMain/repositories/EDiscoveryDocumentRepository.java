package com.koniag.MSExchange.camelExchange.CamelExchangeMain.repositories;

import java.util.Collection;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.koniag.MSExchange.camelExchange.CamelExchangeMain.model.EDiscoveryDocument;

public interface EDiscoveryDocumentRepository extends ElasticsearchRepository<EDiscoveryDocument, String> {

	
	Collection<EDiscoveryDocument> findByAuthor(String author);

}
