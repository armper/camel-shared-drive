package com.PereaTechnologies.CamelExchange.CamelExchangeMain.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jcifs.smb.SmbFile;

@Configuration
@EnableAutoConfiguration 
public class Beans {

	@Bean(name="officeDocumentFilter")
	public OfficeDocumentFilter<SmbFile> OfficeDocumentFilter() {
		return new OfficeDocumentFilter<SmbFile>();
	}
}
