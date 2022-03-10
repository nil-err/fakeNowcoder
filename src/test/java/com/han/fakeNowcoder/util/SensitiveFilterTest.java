package com.han.fakeNowcoder.util;

import com.han.fakeNowcoder.FakeNowcoderApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = FakeNowcoderApplication.class)
class SensitiveFilterTest {

  @Autowired private SensitiveFilter sensitiveFilter;

  @Test
  void init() {}

  @Test
  void filter() {
    String text = "赌赌赌不赌博赌赌赌博嫖娼不嫖娼嫖嫖";
    System.out.println(sensitiveFilter.filter(text));

    text = "※赌※赌※赌※不※赌※博※赌※赌※※赌※博※嫖※娼※不※嫖*※娼※嫖※嫖※";
    System.out.println(sensitiveFilter.filter(text));

    text = "*赌*赌*赌*不*赌*博*赌*赌*赌*博*嫖*娼*不*嫖**娼*嫖**嫖*";
    System.out.println(sensitiveFilter.filter(text));
  }
}
