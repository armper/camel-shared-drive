package com.koniag.MSExchange.camelExchange.CamelExchangeMain.camelRoutes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class FileSystemRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("smb://xxx;xxx/myshare?password=xxx").to("file://target/test-reports");
	}

}
