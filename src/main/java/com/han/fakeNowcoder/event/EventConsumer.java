package com.han.fakeNowcoder.event;

import com.alibaba.fastjson.JSONObject;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Event;
import com.han.fakeNowcoder.entity.Message;
import com.han.fakeNowcoder.service.DiscussPostService;
import com.han.fakeNowcoder.service.ElasticSearchService;
import com.han.fakeNowcoder.service.MessageService;
import com.han.fakeNowcoder.util.CommunityCostant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author imhan
 */
@Component
public class EventConsumer implements CommunityCostant {

  public static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

  @Autowired private MessageService messageService;

  @Autowired private DiscussPostService discussPostService;

  @Autowired private ElasticSearchService elasticSearchService;

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
}
