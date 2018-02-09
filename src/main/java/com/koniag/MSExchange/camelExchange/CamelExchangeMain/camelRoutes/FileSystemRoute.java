package com.koniag.MSExchange.camelExchange.CamelExchangeMain.camelRoutes;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

import jcifs.smb.SmbFile;

@Component
public class FileSystemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
		from("smb://alper@FRED/myshare2?password=ceuVceth!1&idempotent=true").to("file://target/recieved-files")
				.process(new Processor() {
					public void process(Exchange exchange) throws Exception {

						final GenericFile<SmbFile> body = (GenericFile<SmbFile>) exchange.getIn().getBody();

						final String path = "\\\\FRED\\myshare2\\";
						final String filename = body.getFileNameOnly();

						System.out.println(filename);
						Resource resource = new ClassPathResource("script.ps1");

						String script = resource.getFile().getAbsolutePath();
						
						PowerShell powerShell = null;
						try {
							// Creates PowerShell session (we can execute several commands in the same
							// session)
							powerShell = PowerShell.openSession();
							Map<String, String> config = new HashMap<String, String>();
							config.put("maxWait", "80000");

							// Execute a command in PowerShell session
//							PowerShellResponse response = powerShell.executeCommand("Set-Location " + path);
							PowerShellResponse response = powerShell.configuration(config).executeScript(script+" "+path+filename);

							// Print results
							System.out.println("executeScript: " + response.getCommandOutput());

							// Execute another command in the same PowerShell session
//							response = powerShell.executeCommand("Get-ItemProperty " + filename);

							// Print results
							System.out.println("ItemProperty:" + response.getCommandOutput());
						} catch (PowerShellNotAvailableException ex) {
							// Handle error when PowerShell is not available in the system
							// Maybe try in another way?
						} finally {
							// Always close PowerShell session to free resources.
							if (powerShell != null)
								powerShell.close();
						}

					}
				}).end().setId("read da file");

		// from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
		// from("smb://alper@FRED/myshare2?password=ceuVceth!1&idempotent=true").to("file://target/recieved-files?delete=true")
		// .process(new Processor() {
		// public void process(Exchange exchange) throws Exception {
		// final GenericFile<SmbFile> body = (GenericFile<SmbFile>)
		// exchange.getIn().getBody();
		// System.out.println("derp " + body.getFile().getName());
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
	 */
}
