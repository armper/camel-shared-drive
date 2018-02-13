package com.koniag.MSExchange.camelExchange.CamelExchangeMain.configuration;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import jcifs.smb.SmbFile;

@Configuration
@EnableAutoConfiguration 
public class Beans {

	@Bean(name="officeDocumentFilter")
	public OfficeDocumentFilter<SmbFile> OfficeDocumentFilter() {
		return new OfficeDocumentFilter<SmbFile>();
	}
}
