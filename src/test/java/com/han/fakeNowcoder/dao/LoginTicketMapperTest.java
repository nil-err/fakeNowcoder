package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import com.han.fakeNowcoder.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class LoginTicketMapperTest {

  @Autowired private LoginTicketMapper loginTicketMapper;

  @Test
  void insertLoginTicket() {
    LoginTicket loginTicket = new LoginTicket();
    loginTicket.setUserId(101);
    loginTicket.setTicket("abc");
    loginTicket.setStatus(0);
    loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 600));
    int i = loginTicketMapper.insertLoginTicket(loginTicket);
    System.out.println(i);
  }

  @Test
  void selectByTicket() {
    String ticket = "abc";
    LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
    System.out.println(loginTicket);
  }

  @Test
  void updateStatus() {
    String ticket = "abc";
    loginTicketMapper.updateStatus(ticket, 1);
    LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
    System.out.println(loginTicket);
  }
}
