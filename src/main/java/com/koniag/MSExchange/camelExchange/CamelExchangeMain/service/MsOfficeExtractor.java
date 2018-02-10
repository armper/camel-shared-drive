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

	public MsOfficeExtractor(final String[] properties) {
		this.properties = (properties == null ? new String[] {} : properties);
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

	public Map<String, Object> parseMetaData(final byte[] data) {
		if (properties.length == 0) {
			return Collections.EMPTY_MAP;
		}
		
		MetaDataListener metaDataListener = new MetaDataListener();

		InputStream in = null;
		try {
			in = new ByteArrayInputStream(data);
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

}