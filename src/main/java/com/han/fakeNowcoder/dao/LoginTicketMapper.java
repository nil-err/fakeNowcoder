package com.han.fakeNowcoder.dao;

import com.han.fakeNowcoder.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author imhan
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

  /**
   * @param loginTicket 要被插入的ticket
   * @return 行数
   */
  @Insert({
    "insert into login_ticket (user_id, ticket, status, expired) ",
    "values(#{userId}, #{ticket}, #{status}, #{expired})"
  })
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insertLoginTicket(LoginTicket loginTicket);

  /**
   * @param ticket 根据ticket查找对应的LoginTicket
   * @return
   */
  @Select({
    "select id, user_id, ticket, status, expired ",
    "from login_ticket where ticket = #{ticket}"
  })
  LoginTicket selectByTicket(String ticket);

  /**
   * @param ticket 查找对应的ticket
   * @param status 要修改为的状态
   * @return
   */
  @Update({
    "<script> ",
    "update login_ticket set status = #{status} ",
    "where ticket = #{ticket} ",
    "<if test=\"ticket!=null\"> ",
    "and 1=1 ",
    "</if> ",
    "</script>"
  })
  int updateStatus(String ticket, int status);
}
