package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.event.EventProducer;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(ShareController.class);

  @Autowired private EventProducer eventProducer;

  @Value("${nowcoderCustom.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Value("${wk.image.storage}")
  private String wkImageStorage;

  @RequestMapping(path = "/share", method = RequestMethod.GET)
  @ResponseBody
  public String share(String htmlUrl) {

    String filename = CommunityUtil.generateUUID();

    Event event = new Event();
    event
        .setTopic(TOPIC_SHARE)
        .setData("htmlUrl", htmlUrl)
        .setData("filename", filename)
        .setData("suffix", ".png");

    eventProducer.fireEvent(event);

    // 返回访问路径
    Map<String, Object> map = new HashMap<>();
    map.put("shareUrl", domain + contextPath + "/share/image/" + filename);

    return CommunityUtil.getJSONString(0, null, map);
  }

  @RequestMapping(path = "/share/image/{filename}", method = RequestMethod.GET)
  public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
    if (StringUtils.isBlank(filename)) {
      throw new IllegalArgumentException("文件名不能为空");
    }
    // 服务器图片路径
    File file = new File(wkImageStorage + "/" + filename + ".png");

    response.setContentType("image/png");

    try (ServletOutputStream outputStream = response.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file)) {
      byte[] buffer = new byte[1024];
      int b = 0;
      while ((b = fileInputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, b);
      }
    } catch (IOException e) {
      logger.error("响应分享图片出错：" + e.getMessage());
    }
  }
}
