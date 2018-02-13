package com.koniag.MSExchange.camelExchange.CamelExchangeMain;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.koniag.MSExchange.camelExchange.CamelExchangeMain.model.EDiscoveryDocument;
import com.koniag.MSExchange.camelExchange.CamelExchangeMain.repositories.EDiscoveryDocumentRepository;

import lombok.extern.log4j.Log4j;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EDiscoveryDocumentRepositoryTest {
    private static Logger logger = LogManager.getLogger();

	@Autowired
	private EDiscoveryDocumentRepository eDiscoveryDocumentRepository;

	@Autowired
    private ElasticsearchTemplate esTemplate;
	
	private final String testId = UUID.randomUUID().toString();	

	@Before
	public void setup(){
		esTemplate.deleteIndex(EDiscoveryDocument.class);
		esTemplate.createIndex(EDiscoveryDocument.class);
		esTemplate.putMapping(EDiscoveryDocument.class);
		esTemplate.refresh(EDiscoveryDocument.class);
		
		EDiscoveryDocument eDiscoveryDocument = new EDiscoveryDocument();
		eDiscoveryDocument.setId(testId);
		eDiscoveryDocument.setTitle("test title");
		eDiscoveryDocument.setKeywords("test 123 derp haha");
		
		eDiscoveryDocumentRepository.save(eDiscoveryDocument);
		
	}
	
	/**
	 * Tests findOne in eDiscoveryDocumentRepository
	 */
	@Test
	public void testFindOne() {
		EDiscoveryDocument findOne = eDiscoveryDocumentRepository.findOne(testId);
		
		assertThat(findOne.getId()).isEqualTo(testId);
	}

}
