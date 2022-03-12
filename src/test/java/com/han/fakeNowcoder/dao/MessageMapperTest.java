package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import com.han.fakeNowcoder.entity.Message;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class MessageMapperTest {

  @Autowired private MessageMapper messageMapper;

  @Test
  void selectConversations() {
    List<Message> messageList = messageMapper.selectConversations(111, 1, 20);
    for (Message message : messageList) {
      System.out.println(message);
    }
  }

  @Test
  void selectConversationsCount() {
    int i = messageMapper.selectConversationsCount(111);
    System.out.println(i);
  }

  @Test
  void selectMessages() {
    List<Message> messageList = messageMapper.selectMessages("111_112", 1, 20);
    for (Message message : messageList) {
      System.out.println(message);
    }
  }

  @Test
  void selectMessagesCount() {
    int i = messageMapper.selectMessagesCount("111_112");
    System.out.println(i);
  }

  @Test
  void selectUnreadMessagesCount() {
    int i = messageMapper.selectUnreadMessagesCount(111, null);
    System.out.println(i);
    i = messageMapper.selectUnreadMessagesCount(131, "111_131");
    System.out.println(i);
  }
}
