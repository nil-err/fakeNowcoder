package com.han.fakeNowcoder.util;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class MailClientTest {

  @Test
  void sendMail() {}

  @Autowired private MailClient mailClient;

  @Autowired private TemplateEngine templateEngine;

  @Test
  public void testTextMail() {
    mailClient.sendMail("yicircle98@gmail.com", "TEST", "A test mail!");
  }

  @Test
  public void testHtmlMail() {
    Context context = new Context();
    context.setVariable("username", "Yi");

    String content = templateEngine.process("/mail/demo", context);

    System.out.println(content);

    mailClient.sendMail("yicircle98@gmail.com", "HTML", content);
  }
}
