package project.back.configuration;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {
    @Value("${krampoline.host.url}")
    private String proxyHost;

    @Value("${krampoline.host.port}")
    private int proxyPort;

    @Bean
    public WebClient webClient(){

        HttpClient httpClient=HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,0)
                .responseTimeout(Duration.ofMillis(500))
                .doOnConnected(connection -> {
                    connection.addHandlerFirst(new ReadTimeoutHandler(500, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(500,TimeUnit.MILLISECONDS));
                })
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host(proxyHost).port(proxyPort));

        WebClient webClient=WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                /*.baseUrl("https://open-api.bser.io")
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            log.info("requestheader data");
                            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                            clientRequest.headers().forEach(
                                    (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                            );
                            return Mono.just(clientRequest);
                        }

                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(resp->{
                    log.info("Resp data");
                    resp.headers().asHttpHeaders().forEach(
                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                    );
                    return Mono.just(resp);
                }))
                .defaultHeader("x-api-key","NVe4oEcXC86ROegAdN8taIWyTvWbQak5hJtVo1Ad")*/
               // .defaultHeader("Content-type","application/x-www-form-urlencoded;charset=utf-8")
                .build();



        return webClient;
    }
}
