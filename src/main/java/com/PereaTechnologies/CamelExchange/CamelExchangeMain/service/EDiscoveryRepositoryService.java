package com.PereaTechnologies.CamelExchange.CamelExchangeMain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.PereaTechnologies.CamelExchange.CamelExchangeMain.repositories.SearchFileRepository;

@Service
public class EDiscoveryRepositoryService {

	private final SearchFileRepository eDiscoveryDocumentRepository;

	@Autowired
	public EDiscoveryRepositoryService(SearchFileRepository eDiscoveryDocumentRepository) {
		this.eDiscoveryDocumentRepository = eDiscoveryDocumentRepository;
	}

	public SearchFileRepository geteDiscoveryDocumentRepository() {
		return eDiscoveryDocumentRepository;
	}
}
