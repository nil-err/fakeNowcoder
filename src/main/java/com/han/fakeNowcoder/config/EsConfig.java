package com.han.fakeNowcoder.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * @author imhan
 */
@Configuration
public class EsConfig {

  @Value("${elasticsearch.url}")
  private String esUrl;

  @Bean
  RestHighLevelClient client() {
    ClientConfiguration clientConfiguration =
        ClientConfiguration.builder().connectedTo(esUrl).build();
    return RestClients.create(clientConfiguration).rest();
  }
}
