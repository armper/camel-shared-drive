package com.PereaTechnologies.CamelExchange.CamelExchangeMain.repositories;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.PereaTechnologies.CamelExchange.CamelExchangeMain.model.SearchFile;

public interface SearchFileRepository extends PagingAndSortingRepository<SearchFile, UUID> {
	
}
