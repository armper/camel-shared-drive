package com.koniag.MSExchange.camelExchange.CamelExchangeMain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;
import lombok.ToString;

@Data
@Document(indexName = "ediscovery", type="ediscoveryDocument")
@ToString
public class EDiscoveryDocument {

	@Id
	public String id;
	
	public String author;
	
	public String title;
	
	public String keywords;
	
	public String comments;
	
	public String createDateTime;
	
	public String lastSaveDateTime;
}
