package com.han.fakeNowcoder.service;

import com.han.fakeNowcoder.dao.AlphaDao;
import com.han.fakeNowcoder.dao.DiscussPostMapper;
import com.han.fakeNowcoder.dao.UserMapper;
import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.User;
import com.han.fakeNowcoder.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * @author imhan
 */
@Service
// @Scope("prototype")
public class AlphaService {

  @Autowired private AlphaDao alphaDao;

  @Autowired private UserMapper userMapper;

  @Autowired private DiscussPostMapper discussPostMapper;

  @Autowired private TransactionTemplate transactionTemplate;

  //    public AlphaService() {
  //        System.out.println("实例化AlphaService");
  //    }
  //
  //    @PostConstruct
  //    public void init() {
  //        System.out.println("初始化AlphaService");
  //    }
  //
  //    @PreDestroy
  //    public void destoy() {
  //        System.out.println("销毁AlphaService");
  //    }

  public String find() {
    return alphaDao.select();
  }

  /**
   * Propagation.REQUIRED 常用的事务管理中的传播机制： <br>
   * * ```Propagation.REQUIRED``` ：支持当前事务（外部事务，即调用者），如果不存在则创建新事务 <br>
   * * ```Propagation.REQUIRES_NEW``` ： 创建一个新事务，并暂停当前事务 <br>
   * * ```Propagation.NESTED``` ： 如果存在当前事务，则嵌套在该事务中执行（具有独立的提交和回滚），否则与```REQUIRED```一样
   *
   * @return
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public Object save1() {
    // 新增用户
    User user = new User();
    user.setUsername("alpha");
    user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
    user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
    user.setEmail("Abc@cc.com");
    user.setHeaderUrl("http://image.nowcoder.com/head/10t.png");
    user.setCreatTime(new Date());
    userMapper.insertUser(user);

    // 新增帖子
    DiscussPost discussPost = new DiscussPost();
    discussPost.setUserId(user.getId());
    discussPost.setTitle("Hello");
    discussPost.setContent("hello !");
    discussPost.setCreateTime(new Date());
    discussPostMapper.insertDiscussPost(discussPost);

    // 人为出个错，看事务管理的回滚
    Integer.valueOf("abc");

    return "ok";
  }

  public Object save2() {
    transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    return transactionTemplate.execute(
        new TransactionCallback<Object>() {
          @Override
          public Object doInTransaction(TransactionStatus status) {
            // 新增用户
            User user = new User();
            user.setUsername("beta");
            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
            user.setEmail("beta@cc.com");
            user.setHeaderUrl("http://image.nowcoder.com/head/10t.png");
            user.setCreatTime(new Date());
            userMapper.insertUser(user);

            // 新增帖子
            DiscussPost discussPost = new DiscussPost();
            discussPost.setUserId(user.getId());
            discussPost.setTitle("HHHHello");
            discussPost.setContent("hhhhello !");
            discussPost.setCreateTime(new Date());
            discussPostMapper.insertDiscussPost(discussPost);

            // 人为出个错，看事务管理的回滚
            Integer.valueOf("abc");

            return "ok";
          }
        });
  }
}
