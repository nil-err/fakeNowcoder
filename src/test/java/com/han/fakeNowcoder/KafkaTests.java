package com.han.fakeNowcoder;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
public class KafkaTests {
  @Autowired KafkaProducer kafkaProducer;

  @Test
  public void testKafka() {
    kafkaProducer.sendMessage("test", "A");
    kafkaProducer.sendMessage("test", "B");
    kafkaProducer.sendMessage("test", "C");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

@Component
class KafkaProducer {

  @Autowired KafkaTemplate kafkaTemplate;

  public void sendMessage(String topic, String content) {
    kafkaTemplate.send(topic, content);
  }
}

@Component
class KafkaComsumer {

  @KafkaListener(topics = {"test"})
  public void handleMessage(ConsumerRecord record) {
    System.out.println(record.value());
  }
}
