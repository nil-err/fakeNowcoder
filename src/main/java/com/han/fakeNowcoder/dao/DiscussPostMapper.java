package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author imhan
 */
@Mapper
public interface DiscussPostMapper {

  /**
   * @param userId 某个id发的帖子，用户开发个人主页
   * @param offset 当前页的起始行，用于开发分页插件
   * @param limit 每页显示多少行，用于开发分页插件
   * @return
   */
  List<DiscussPost> selectDiscussPosts(
      @Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

  /**
   * @param userId 如果某个SQL使用动态的参数SQL中使用<if>，并且只有一个参数，那么必须取别名
   * @return
   */
  int selectDiscussPostRows(@Param("userId") int userId);

  int insertDiscussPost(@Param("discussPost") DiscussPost discussPost);

  DiscussPost selectDiscussPostById(@Param("id") int id);

  int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

  int updateType(@Param("id") int id, @Param("type") int type);

  int updateStatus(@Param("id") int id, @Param("status") int status);
}
