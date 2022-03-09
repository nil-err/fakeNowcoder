package com.han.fakeNowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author imhan
 */
@Component
public class MailClient {

  private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

  @Autowired private JavaMailSender javaMailSender;

  @Value("${spring.mail.username}")
  private String sender;

  public void sendMail(String receiver, String subject, String content) {

    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
      mimeMessageHelper.setFrom(sender);
      mimeMessageHelper.setTo(receiver);
      mimeMessageHelper.setSubject(subject);
      mimeMessageHelper.setText(content, true);
      javaMailSender.send(mimeMessageHelper.getMimeMessage());
    } catch (MessagingException e) {
      logger.error("邮件发送失败" + e.getMessage());
    }
  }
}
