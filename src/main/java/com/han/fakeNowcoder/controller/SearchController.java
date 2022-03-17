package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.entity.DiscussPost;
import com.han.fakeNowcoder.entity.Page;
import com.han.fakeNowcoder.entity.SearchResult;
import com.han.fakeNowcoder.service.ElasticSearchService;
import com.han.fakeNowcoder.service.LikeService;
import com.han.fakeNowcoder.service.UserService;
import com.han.fakeNowcoder.util.CommunityCostant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityCostant {

  private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

  @Autowired private ElasticSearchService elasticSearchService;

  @Autowired private UserService userService;

  @Autowired private LikeService likeService;

  /**
   * search?keyword=xxx
   *
   * @param keyword 搜索关键词
   * @param page 分页信息
   * @param model
   * @return
   */
  @RequestMapping(path = "/search", method = RequestMethod.GET)
  public String search(String keyword, Page page, Model model) {
    // 搜索帖子
    try {
      SearchResult searchResult =
          elasticSearchService.searchDiscussPost(
              keyword, (page.getCurrent() - 1) * page.getLimit(), page.getLimit());

      List<Map<String, Object>> discussPosts = new ArrayList<>();

      List<DiscussPost> list = searchResult.getList();

      if (list != null) {
        for (DiscussPost discussPost : list) {
          Map<String, Object> map = new HashMap<>();
          // 帖子 和 作者
          map.put("discussPost", discussPost);
          map.put("user", userService.findUserById(discussPost.getUserId()));
          // 点赞数目
          map.put(
              "likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));

          discussPosts.add(map);
        }
      }
      model.addAttribute("discussPosts", discussPosts);
      model.addAttribute("keyword", keyword);

      // 分页信息
      page.setPath("/search?keyword=" + keyword);
      page.setRows(searchResult.getTotal() == 0 ? 0 : (int) searchResult.getTotal());

    } catch (IOException e) {
      logger.error("搜索出错：" + e.getMessage());
      e.printStackTrace();
    }

    return "/site/search";
  }
}
