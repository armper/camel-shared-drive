package com.koniag.MSExchange.camelExchange.CamelExchangeMain.configuration;

import java.util.Arrays;
import java.util.Collection;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public class OfficeDocumentFilter<SmbFile> implements GenericFileFilter<SmbFile> {

	@Value("${office-document-types}")
	private String[] officeDocumentTypes;

	public boolean accept(GenericFile<SmbFile> file) {
		Collection<String> collection = Arrays.asList(officeDocumentTypes);

		return collection.stream()
				.anyMatch(officeDocumentType -> StringUtils.endsWithIgnoreCase(file.getFileName(), officeDocumentType));
	}
}
