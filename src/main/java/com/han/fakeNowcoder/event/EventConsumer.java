package com.han.fakeNowcoder.event;

import com.alibaba.fastjson.JSONObject;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.entity.Message;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.ElasticSearchService;
import com.han.fakeNowcoder.service.MessageService;
import com.han.fakeNowcoder.util.CommunityCostant;
import com.han.fakeNowcoder.util.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author imhan
 */
@Component
public class EventConsumer implements CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

  @Autowired private MessageService messageService;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private ElasticSearchService elasticSearchService;

  @Value("${wk.image.storage}")
  private String wkImageStorage;

  @Value("${wk.image.command}")
  private String wkImageCommand;

  @Value("${qiniu.key.access}")
  private String accessKey;

  @Value("${qiniu.key.secret}")
  private String secretKey;

  @Value("${qiniu.bucket.share.name}")
  private String shareBucketName;

  @Value("${qiniu.bucket.share.url}")
  private String shareBucketUrl;

  @Autowired private ThreadPoolTaskScheduler taskScheduler;

  @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
  public void handleMessageOfCommentLikeAndFollow(ConsumerRecord record) {
    if (record == null || record.value() == null) {
      logger.error("消息内容为空！");
      return;
    }

    Event event = JSONObject.parseObject(record.value().toString(), Event.class);

    if (event == null) {
      logger.error("消息格式有误！");
      return;
    }

    // 发送站内通知
    Message message = new Message();
    message.setFromId(SYSTEM_USER_ID);
    message.setToId(event.getEntityUserId());
    message.setConversationId(event.getTopic());
    message.setCreateTime(new Date());

    Map<String, Object> content = new HashMap<>();
    content.put("userId", event.getUserId());
    content.put("entityType", event.getEntityType());
    content.put("entityId", event.getEntityId());

    if (!event.getData().isEmpty()) {
      for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
        content.put(entry.getKey(), entry.getValue());
      }
    }

    message.setContent(JSONObject.toJSONString(content));

    messageService.addMessage(message);
  }

  // 消费发帖事件
  @KafkaListener(topics = {TOPIC_PUBLISH})
  public void handlePublishDiscussPost(ConsumerRecord record) {
    if (record == null || record.value() == null) {
      logger.error("消息内容为空！");
      return;
    }

    Event event = JSONObject.parseObject(record.value().toString(), Event.class);

    if (event == null) {
      logger.error("消息格式有误！");
      return;
    }

    DiscussPost discussPost = discussPostService.findDiscussPostById(event.getEntityId());
    elasticSearchService.saveDiscussPost(discussPost);
  }
  // 消费删帖事件
  @KafkaListener(topics = {TOPIC_DELETE})
  public void handleDeleteDiscussPost(ConsumerRecord record) {
    if (record == null || record.value() == null) {
      logger.error("消息内容为空！");
      return;
    }

    Event event = JSONObject.parseObject(record.value().toString(), Event.class);

    if (event == null) {
      logger.error("消息格式有误！");
      return;
    }

    elasticSearchService.deleteDiscussPost(event.getEntityId());
  }

  // 消费分享事件
  @KafkaListener(topics = {TOPIC_SHARE})
  public void handleShareMessage(ConsumerRecord record) {
    if (record == null || record.value() == null) {
      logger.error("消息内容为空！");
      return;
    }

    Event event = JSONObject.parseObject(record.value().toString(), Event.class);

    if (event == null) {
      logger.error("消息格式有误！");
      return;
    }

    Map<String, Object> data = event.getData();
    String htmlUrl = (String) data.get("htmlUrl");
    String filename = (String) data.get("filename");
    String suffix = (String) data.get("suffix");

    String command =
        wkImageCommand
            + " --quality 75 "
            + htmlUrl
            + " "
            + wkImageStorage
            + "/"
            + filename
            + suffix;

    try {
      Runtime.getRuntime().exec(command);
      logger.info("生成图片成功 ： " + command);
    } catch (IOException e) {
      logger.error("生成图片失败 ： " + e.getMessage());
    }
    /*Runtime.getRuntime().exec(command);是异步执行，并且会比较慢，因此后面的逻辑会先执行完
     * 使用ThreadPoolTaskScheduler定时查询图片文件是否生成*/

    UploadTask uploadTask = new UploadTask(filename, suffix);
    Future future = taskScheduler.scheduleAtFixedRate(uploadTask, 500);
    uploadTask.setFuture(future);
  }

  class UploadTask implements Runnable {
    // 文件名
    private String filename;
    // 文件后缀
    private String suffix;
    // 启动任务的返回值，可以用于停止方法
    private Future future;
    // 任务开始事件
    private long startTime;
    // 上传次数
    private int uploadCount;

    public UploadTask(String filename, String suffix) {
      this.filename = filename;
      this.suffix = suffix;
      this.startTime = System.currentTimeMillis();
    }

    public void setFuture(Future future) {
      this.future = future;
    }

    @Override
    public void run() {
      // 生成图片失败
      if (System.currentTimeMillis() - startTime > 30000) {
        logger.error("执行时间过长，终止任务：" + filename);
        future.cancel(true);
        return;
      }
      // 上传图片失败
      if (uploadCount >= 3) {
        logger.error("上传次数过多，终止任务：" + filename);
        future.cancel(true);
        return;
      }
      String path = wkImageStorage + "/" + filename + suffix;
      File file = new File(path);
      if (file.exists()) {
        logger.info(String.format("开始第%d次上传[%s]", ++uploadCount, filename));
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成往七牛云的上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(shareBucketName, filename, 3600, policy);
        // 指定上传机房
        UploadManager uploadManager = new UploadManager(new Configuration(Zone.zone1()));
        // 开始上传图片
        try {
          Response response =
              uploadManager.put(path, filename, uploadToken, null, "image/" + suffix, false);
          // 处理响应结果
          JSONObject jsonObject = JSONObject.parseObject(response.bodyString());
          if (jsonObject == null
              || jsonObject.get("code") == null
              || !jsonObject.get("code").toString().equals("0")) {
            logger.info(String.format("第%d次上传失败[%s]", uploadCount, filename));
          } else {
            logger.info(String.format("第%d上传成功[%s]", uploadCount, filename));
            future.cancel(true);
          }
        } catch (QiniuException e) {
          logger.info(String.format("第%d次上传失败[%s] EXCEPTION", uploadCount, filename));
        }
      } else {
        logger.info(String.format("等待图片生成[" + filename + "]"));
      }
    }
  }
}
