package com.han.fakeNowcoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * @author imhan
 */
@SpringBootApplication
public class FakeNowcoderApplication {

  @PostConstruct
  public void init() {
    // 解决Netty启动冲突的问题
    // Netty4Utils.setAvailableProcessors()
    System.setProperty("es.set.netty.runtime.available.processors", "false");
  }

  public static void main(String[] args) {
    SpringApplication.run(FakeNowcoderApplication.class, args);
  }
}
