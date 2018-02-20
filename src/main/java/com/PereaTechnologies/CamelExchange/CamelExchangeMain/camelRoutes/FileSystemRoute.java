package com.PereaTechnologies.CamelExchange.CamelExchangeMain.camelRoutes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.PereaTechnologies.CamelExchange.CamelExchangeMain.model.SearchFile;
import com.PereaTechnologies.CamelExchange.CamelExchangeMain.model.SearchUser;
import com.PereaTechnologies.CamelExchange.CamelExchangeMain.repositories.SearchFileRepository;
import com.PereaTechnologies.CamelExchange.CamelExchangeMain.service.MsOfficeExtractor;
import com.profesorfalken.wmi4java.WMI4Java;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class FileSystemRoute extends RouteBuilder {
	private static Logger logger = LogManager.getLogger();
	private final SearchFileRepository searchFileRepository;

	@Autowired
	public FileSystemRoute(SearchFileRepository eDiscoveryDocumentRepository) {
		super();
		this.searchFileRepository = eDiscoveryDocumentRepository;
	}

	@Override
	public void configure() throws Exception {

		SmbFile[] domains = null;

		try {
			logger.info("Finding all shared drives...");
//			SmbFile file = new SmbFile("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/");
			SmbFile file = new SmbFile("smb://alper:ceuVceth!1@FRED/");
			domains = file.listFiles();

		} catch (SmbException e1) {
			logger.error("error " + e1.getMessage());
		}

		log.debug("found: " + domains.length + " SMBFiles. Will remove ones with $");

		new ArrayList<SmbFile>(Arrays.asList(domains)).stream().forEach(d -> logger.debug(d.getPath()));

		Set<String> domainSet = new ArrayList<SmbFile>(Arrays.asList(domains)).stream().filter(predicate -> {
			try {
				return predicate.canRead();
			} catch (Exception e) {
				logger.warn("Cannot read" + predicate.getPath());
				return false;
			}

		}).map(mapper -> mapper.getPath()).filter(p -> !StringUtils.endsWith(p, "$/")).collect(Collectors.toSet());

		domainSet.stream().forEach(d -> logger.debug("Will scan: " + d));

		logger.debug("End getting domains.");

		if (domainSet.size() > 0)
			logger.debug("Begin setting up camel routes based upon collected domains.");
		else
			logger.debug("no domains :(");

		domainSet.forEach(d -> {

			String routeId = UUID.randomUUID().toString();

			// from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
			from(d + "?password=ceuVceth!1&idempotent=true&filter=#officeDocumentFilter&recursive=false&noop=true")
					.to("file://target/recieved-files/" + routeId).process(new Processor() {

						public void process(Exchange exchange) throws Exception {

							final GenericFile<SmbFile> body = (GenericFile<SmbFile>) exchange.getIn().getBody();

							String fileName = body.getFileNameOnly();
							logger.debug("SmbFile filename is " + fileName);

							String extension = FilenameUtils.getExtension(fileName);
							logger.debug("SmbFile extension is " + extension);

							logger.debug("security "+body.getFile().getSecurity().toString());
							//Example get processes named java.exe
//						    Map<String, String> wmiObjectProperties = 
//						        WMI4Java.get().filters(Arrays.asList("where name=\""+body.getAbsoluteFilePath()+"\"")).getWMIObject("CIM_DataFile");
//						    
//							log.debug("derp " + wmiObjectProperties.toString());

							SearchFile searchFile = null;

							MsOfficeExtractor msOfficeExtractor = new MsOfficeExtractor();

							// get byte array of any MS office document
							InputStream is = body.getFile().getInputStream();

							Collection<String> office97Types = Arrays.asList("doc", "xls", "ppt");
							Collection<String> office2003Types = Arrays.asList("docx", "xlsx", "pptx");

							if (office97Types.contains(extension)) {
								searchFile = msOfficeExtractor.getFromOffice97(is);
							} else if (extension.equals("xlsx")) {
								searchFile = msOfficeExtractor.getFromOffice2003(is);
							}

							if (searchFile != null) {
								logger.info("Here is the model: eDiscoveryDocument: " + searchFile.toString());
								
								searchFile.setFileName(fileName);
								searchFile.setExtension(extension);
								
								SearchUser createdBy=new SearchUser();
								createdBy.setName("aperea");
								
								searchFile.setCreatedBy(createdBy);
								
								searchFileRepository.save(searchFile);

								logger.info("in db: "
										+ searchFileRepository.findOne(searchFile.getId()).toString());
							} else
								logger.info("File extension " + extension
										+ " is not implemented, or there was no data found in the document.");

						}
					})
					// .end().process(new Processor() {
					//
					// @Override
					// public void process(Exchange exchange) throws Exception {
					// exchange.getIn().getBody();
					// log.info("--------Report--------");
					// Collection<EDiscoveryDocument> byAuthor =
					// eDiscoveryDocumentRepository.findByAuthor("Armando Perea");
					// byAuthor.stream().forEach(a->{
					// log.info("Titles by Armando Perea:"+a.getTitle());
					// });
					//
					// Iterable<EDiscoveryDocument> findAllSortByTitle =
					// eDiscoveryDocumentRepository.findAll();
					//
					// findAllSortByTitle.forEach(a->{
					// log.info(a.getAuthor()+" authored title: "+a.getTitle());
					// });
					//
					// }
					// })
					.end().setId("SMB_route_" + routeId);

		});

		// from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
		// from("smb://alper@FRED/myshare2?password=ceuVceth!1&idempotent=true").to("file://target/recieved-files")
		// .process(new Processor() {
		// public void process(Exchange exchange) throws Exception {
		// final GenericFile<SmbFile> body = (GenericFile<SmbFile>)
		// exchange.getIn().getBody();
		//
		// final String path = "//FRED/myshare2/";
		// final String filename = body.getFileNameOnly();
		//
		// // initialize extractor
		// String[] poiProperties = new String[] { "Title", "Author", "Keywords",
		// "Comments",
		// "CreateDateTime", "LastSaveDateTime" };
		// MsOfficeExtractor msOfficeExtractor = new MsOfficeExtractor(poiProperties);
		//
		// // get byte array of any MS office document
		// InputStream is = body.getFile().getInputStream();
		// byte[] data = IOUtils.toByteArray(is);
		//
		// // extract metadata
		// Map<String, Object> metadata = msOfficeExtractor.parseMetaData(data);
		//
		// if (metadata == null) {
		// logger.info("Metadata null");
		// return;
		// }
		// logger.info("Title: " + metadata.get("Title"));
		// logger.info("Author: " + metadata.get("Author"));
		// logger.info("Keywords: " + metadata.get("Keywords"));
		// logger.info("Comments: " + metadata.get("Comments"));
		// logger.info("CreateDateTime: " + metadata.get("CreateDateTime"));
		// logger.info("LastSaveDateTime: " + metadata.get("LastSaveDateTime"));
		// }
		// }).end().setId("read da file");

		// from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
		// from("smb://alper@FRED/myshare2?password=ceuVceth!1&idempotent=true").to("file://target/recieved-files?delete=true")
		// .process(new Processor() {
		// public void process(Exchange exchange) throws Exception {
		// final GenericFile<SmbFile> body = (GenericFile<SmbFile>)
		// exchange.getIn().getBody();
		// logger.info("derp " + body.getFile().getName());
		// }
		// }).end().setId("read da file");
		// ;
	}
	/*
	 * 
	 * from("file:{{folder.status-files.root}}{{file.consumer.common.properties}}").
	 * log("agent-npn start")
	 * .unmarshal(npnDataformat).removeProperty("CamelFileExchangeFile").split().
	 * simple("${body}")
	 * .setHeader("agentNumber").simple("${body.agentNumber}").setHeader(
	 * "nationalProducerNumber") .simple("${body.nationalProducerNumber}").to(
	 * "bean:npnRead?method=loadnpndata").end()
	 * .log("agent-npn extract done").setId("beanIOListSplitter");
	 * 
	 * 
	 * 
	 * powershell stuff
	 * 
	 * 
	 * // logger.info(filename); // Resource resource = new
	 * ClassPathResource("script.ps1"); // // String script =
	 * resource.getFile().getAbsolutePath(); // // PowerShell powerShell = null; //
	 * try { // // Creates PowerShell session (we can execute several commands in
	 * the same // // session) // powerShell = PowerShell.openSession(); //
	 * Map<String, String> config = new HashMap<String, String>(); //
	 * config.put("maxWait", "80000"); // // // Execute a command in PowerShell
	 * session //// PowerShellResponse response =
	 * powerShell.executeCommand("Set-Location " + // path); // PowerShellResponse
	 * response = // powerShell.configuration(config).executeScript(script,
	 * path+filename); // // // Print results // logger.info("executeScript: " +
	 * response.getCommandOutput()); // // // } catch
	 * (PowerShellNotAvailableException ex) { // // Handle error when PowerShell is
	 * not available in the system // // Maybe try in another way? // } finally { //
	 * // Always close PowerShell session to free resources. // if (powerShell !=
	 * null) // powerShell.close(); // }
	 */

	public SearchFileRepository geteDiscoveryDocumentRepository() {
		return searchFileRepository;
	}

	// START SNIPPET: e1

}
