package com.assignment.notificationservice.config.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.assignment.notificationservice.repository")
@ComponentScan(basePackages = {"com.assignment.notificationservice"})
public class Config extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.url}")
    public String elasticsearchUrl;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200)).setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder
                            .setMaxConnTotal(20)
                            .setMaxConnPerRoute(10)
                            .setDefaultIOReactorConfig(
                                    IOReactorConfig
                                            .custom()
                                            .setIoThreadCount(20)
                                            .build()
                            );
                    return httpAsyncClientBuilder;
                }));
    }
}