package com.han.fakeNowcoder;

import com.han.fakeNowcoder.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
public class TransactionTests {

  @Autowired private AlphaService alphaService;

  @Test
  public void tsetSave1() {
    Object o = alphaService.save1();
    System.out.println(o);
  }

  @Test
  public void tsetSave2() {
    Object o = alphaService.save2();
    System.out.println(o);
  }
}
