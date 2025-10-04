package com.example.MicroService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.net.URI;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {
	private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URI requestUri = exchange.getRequest().getURI();
		logger.info(" MICROSERVICE LOG - Incoming request URI: {}", requestUri);
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			logger.info(" MICROSERVICE LOG Response status code: {}", exchange.getResponse().getStatusCode());
		}));
	}

	@Override
	public int getOrder() {
		// Filter execution priority: lower = earlier
		return 1;
	}
}