package com.koniag.MSExchange.camelExchange.CamelExchangeMain.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

import java.io.FileInputStream;
import java.io.*;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.commons.io.IOUtils;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.*;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.*;

import com.koniag.MSExchange.camelExchange.CamelExchangeMain.model.EDiscoveryDocument;

import jcifs.smb.SmbFile;
import lombok.NonNull;

public class MsOfficeExtractor {

	public class MetaDataListener implements POIFSReaderListener {
		public final Map<String, Object> metaData;

		public MetaDataListener() {
			metaData = new HashMap<String, Object>();
		}

		public void processPOIFSReaderEvent(final POIFSReaderEvent event) {
			try {
				final SummaryInformation summaryInformation = (SummaryInformation) PropertySetFactory
						.create(event.getStream());

				for (int i = 0; i < properties.length; i++) {
					Method method = (Method) methodMap.get(properties[i]);
					Object propertyValue = method.invoke(summaryInformation, (Object[]) (Object[]) null);

					metaData.put(properties[i], propertyValue);
				}
			} catch (final Exception e) {
				// error handling
			}
		}
	}

	private String[] properties;

	private Map<String, Method> methodMap;

	public MsOfficeExtractor() {

		String[] poiProperties = new String[] { "Title", "Author", "Keywords", "Comments", "CreateDateTime",
				"LastSaveDateTime" };

		this.properties = (poiProperties == null ? new String[] {} : poiProperties);
		methodMap = new HashMap<String, Method>();
		try {
			for (int i = 0; i < properties.length; i++) {
				methodMap.put(properties[i], SummaryInformation.class.getMethod("get" + properties[i], (Class[]) null));
			}
		} catch (SecurityException e) {
			// error handling
		} catch (NoSuchMethodException e) {
			// error handling
		}
	}

	public Map<String, Object> parseMetaData(final byte[] fileData) {
		if (properties.length == 0) {
			return Collections.EMPTY_MAP;
		}

		MetaDataListener metaDataListener = new MetaDataListener();

		InputStream in = null;
		try {
			in = new ByteArrayInputStream(fileData);
			POIFSReader poifsReader = new POIFSReader();
			poifsReader.registerListener(metaDataListener, "\005SummaryInformation");
			poifsReader.read(in);

		} catch (final IOException e) {
			// error handling
		} catch (final RuntimeException e) {
			// error handling
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
		return metaDataListener.metaData;
	}

	public EDiscoveryDocument getFromOffice97(InputStream is) {
		byte[] fileData = null;
		try {
			fileData = IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		Map<String, Object> metadata = this.parseMetaData(fileData);

		EDiscoveryDocument eDiscoveryDocument = new EDiscoveryDocument();

		eDiscoveryDocument.setTitle("" + metadata.get("Title"));
		eDiscoveryDocument.setAuthor("" + metadata.get("Author"));
		eDiscoveryDocument.setKeywords("" + metadata.get("Keywords"));
		eDiscoveryDocument.setComments("" + metadata.get("Comments"));
		eDiscoveryDocument.setCreateDateTime("" + metadata.get("CreateDateTime"));
		eDiscoveryDocument.setLastSaveDateTime("" + metadata.get("LastSaveDateTime"));

		return eDiscoveryDocument;
	}

	public EDiscoveryDocument getFromOffice2003(InputStream is) {
		EDiscoveryDocument eDiscoveryDocument = new EDiscoveryDocument();

		XSSFWorkbook readMetadata = null;
		POIXMLProperties props;
		try {
			readMetadata = new XSSFWorkbook(is);
			props = readMetadata.getProperties();

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} // Read the Excel Workbook in a instance object
		finally {
			try {
				readMetadata.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

		POIXMLProperties.CoreProperties coreProp = props.getCoreProperties();
		/* Read and print core properties as SOP */
		System.out.println("Document Creator :" + coreProp.getCreator());
		System.out.println("Description :" + coreProp.getDescription());
		System.out.println("Keywords :" + coreProp.getKeywords());
		System.out.println("Title :" + coreProp.getTitle());
		System.out.println("Subject :" + coreProp.getSubject());
		System.out.println("Category :" + coreProp.getCategory());

		/* Read and print extended properties */
		POIXMLProperties.ExtendedProperties extProp = props.getExtendedProperties();
		System.out.println("Company :" + extProp.getUnderlyingProperties().getCompany());
		System.out.println("Template :" + extProp.getUnderlyingProperties().getTemplate());
		System.out.println("Manager Name :" + extProp.getUnderlyingProperties().getManager());

		/* Finally, we can retrieve some custom Properies */
		POIXMLProperties.CustomProperties custProp = props.getCustomProperties();
		List<CTProperty> my1 = custProp.getUnderlyingProperties().getPropertyList();
		System.out.println("Size :" + my1.size());
		for (int i = 0; i < my1.size(); i++) {
			CTProperty pItem = my1.get(i);
			System.out.println("" + pItem.getPid());
			System.out.println("" + pItem.getFmtid());
			System.out.println("" + pItem.getName());
			System.out.println("" + pItem.getLpwstr());

		}

		eDiscoveryDocument.setTitle("" + coreProp.getTitle());
		eDiscoveryDocument.setAuthor("" + coreProp.getCreator());
		eDiscoveryDocument.setKeywords("" + coreProp.getKeywords());
		eDiscoveryDocument.setComments("none :(");
		eDiscoveryDocument.setCreateDateTime("" + coreProp.getCreated());
		eDiscoveryDocument.setLastSaveDateTime("" + coreProp.getModified());

		return eDiscoveryDocument;
	}

}