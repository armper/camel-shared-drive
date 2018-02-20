package com.PereaTechnologies.CamelExchange.CamelExchangeMain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.PereaTechnologies.CamelExchange.CamelExchangeMain.model.SearchFile;
import com.PereaTechnologies.CamelExchange.CamelExchangeMain.repositories.SearchFileRepository;

import lombok.extern.log4j.Log4j;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EDiscoveryDocumentRepositoryTest {
	private static Logger logger = LogManager.getLogger();

	@Autowired
	private SearchFileRepository searchFileRepository;

	private UUID testId;

	@Before
	public void setup() {

		SearchFile searchFile = new SearchFile();
		searchFile.setFileName("test filename");
		searchFile.setExtension("doc");

		searchFile = searchFileRepository.save(searchFile);
		
		testId = searchFile.getId();
		
		logger.debug("persisted: "+searchFile);

	}

	/**
	 * Tests findOne in eDiscoveryDocumentRepository
	 */
	@Test
	public void testFindOne() {
		SearchFile findOne = searchFileRepository.findOne(testId);
		
		logger.debug("findOne: "+findOne);

		assertThat(findOne.getId()).isEqualTo(testId);
	}

}
