package com.koniag.MSExchange.camelExchange.CamelExchangeMain.camelRoutes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

@Component
public class FileSystemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
//		from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/recieved-files");
		from("smb://alper@FRED/myshare2?password=ceuVceth!1").process(new Processor() {
			  public void process(Exchange exchange) throws Exception {
				    GenericFile body = (GenericFile) exchange.getIn().getBody();
				    System.out.println("derp "+body.getFileName());
				  }
				}).end();
	}
/*
 * 
		from("file:{{folder.status-files.root}}{{file.consumer.common.properties}}").log("agent-npn start")
				.unmarshal(npnDataformat).removeProperty("CamelFileExchangeFile").split().simple("${body}")
				.setHeader("agentNumber").simple("${body.agentNumber}").setHeader("nationalProducerNumber")
				.simple("${body.nationalProducerNumber}").to("bean:npnRead?method=loadnpndata").end()
				.log("agent-npn extract done").setId("beanIOListSplitter");

 */
}
