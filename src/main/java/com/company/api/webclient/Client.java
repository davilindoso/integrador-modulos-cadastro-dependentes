package com.company.api.webclient;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.company.api.model.Cliente;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

@Component
public class Client {

	WebClient webClient;

	@Autowired
	WebClient.Builder builder;

	@Value("${api.endpoint.cliente}")
	private String endpointCliente;

	@PostConstruct
	public void init() {
		final HttpClient httpClient = HttpClient.create()
				.tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 99 * 1000))
				.tcpConfiguration(client -> client.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(99))
						.addHandlerLast(new WriteTimeoutHandler(99))));
		final ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
		webClient = builder.baseUrl(endpointCliente).clientConnector(connector).build();
	}

	public Flux<Cliente> obterClientes() {
		return webClient.get().retrieve().bodyToFlux(Cliente.class);
	}

}
