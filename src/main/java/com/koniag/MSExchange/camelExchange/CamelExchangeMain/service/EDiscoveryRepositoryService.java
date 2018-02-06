package com.koniag.MSExchange.camelExchange.CamelExchangeMain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.koniag.MSExchange.camelExchange.CamelExchangeMain.repositories.EDiscoveryDocumentRepository;

@Service
public class EDiscoveryRepositoryService {

	private final EDiscoveryDocumentRepository eDiscoveryDocumentRepository;

	@Autowired
	public EDiscoveryRepositoryService(EDiscoveryDocumentRepository eDiscoveryDocumentRepository) {
		this.eDiscoveryDocumentRepository = eDiscoveryDocumentRepository;
	}

	public EDiscoveryDocumentRepository geteDiscoveryDocumentRepository() {
		return eDiscoveryDocumentRepository;
	}
}
