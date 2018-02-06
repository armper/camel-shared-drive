package com.koniag.MSExchange.camelExchange.CamelExchangeMain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Data
@Document(indexName = "ediscovery", type="ediscoveryDocument")
public class EDiscoveryDocument {

	@Id
	public String id;
	
	public String title;
	
	public String body;
}
