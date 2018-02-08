package com.koniag.MSExchange.camelExchange.CamelExchangeMain.camelRoutes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileSystemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
//		from("smb://alper@FRED/myshare2?password=ceuVceth!1").to("file://target/test-reports");
		from("smb://AMER;devsu:C$C123$dev@cscgsxaus1v15/public/amertest").to("file://target/test-reports");

	}

}
