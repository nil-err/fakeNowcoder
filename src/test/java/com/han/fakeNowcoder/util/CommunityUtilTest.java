package com.han.fakeNowcoder.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

class CommunityUtilTest {

  @Autowired private CommunityUtil communityUtil;

  @Test
  void generateUUID() {}

  @Test
  void md5() {}

  @Test
  void getJSONString() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "LI");
    map.put("age", 24);
    int code = 1;
    String msg = "ok";
    String jsonString = CommunityUtil.getJSONString(code, msg, map);
    System.out.println(jsonString);
  }
}
