package com.han.fakeNowcoder.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

  // 前缀树 内部类
  private class TrieNode {
    // 是否是敏感词结束位置
    private boolean isKeywordEnd = false;
    // 子节点(子节点字符，子节点)
    private final Map<Character, TrieNode> children = new HashMap<>();

    public boolean isKeywordEnd() {
      return isKeywordEnd;
    }

    public void setKeywordEnd(boolean keywordEnd) {
      isKeywordEnd = keywordEnd;
    }

    // 添加子节点
    public void addChild(Character c, TrieNode node) {
      children.put(c, node);
    }

    // 获取子节点
    public TrieNode getChild(Character c) {
      return children.getOrDefault(c, null);
    }
  }

  public static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

  // 替换符
  public static final String REPLACEMENT = "*****";

  // 前缀树根节点
  TrieNode root = new TrieNode();

  @PostConstruct
  public void init() {
    try (InputStream inputStream =
            this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String keyword;
      while ((keyword = reader.readLine()) != null) {
        this.addKeyWord(keyword);
      }
    } catch (IOException e) {
      logger.error("读取敏感词库失败： " + e.getMessage());
    }
  }

  private void addKeyWord(String keyword) {
    TrieNode p = root;
    for (int i = 0; i < keyword.length(); i++) {
      char c = keyword.charAt(i);
      TrieNode node = p.getChild(c);
      if (node == null) {
        node = new TrieNode();
        p.addChild(c, node);
      }
      if (i == keyword.length() - 1) {
        node.setKeywordEnd(true);
      }
      p = node;
    }
  }

  private boolean isSymbol(char c) {
    // 0x2E80 ~ 0x9FFF 东亚文字符号
    return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
  }

  /**
   * @param text 待过滤文本
   * @return 过滤后的文本
   */
  public String filter(String text) {
    if (StringUtils.isBlank(text)) {
      return text;
    }
    int len = text.length();
    TrieNode p = root;
    int begin = 0;
    int end = 0;
    StringBuilder sb = new StringBuilder();

    while (begin < len) {
      if (end < len) {
        char c = text.charAt(end);

        // 跳过符号
        if (isSymbol(c)) {
          // 如果p在根节点，直接把符号加入sb
          if (p == root) {
            begin++;
            sb.append(c);
          }
          end++;
          continue;
        }

        if (p.children.containsKey(c)) {
          p = p.children.get(c);
          if (p.isKeywordEnd) {
            sb.append(REPLACEMENT);
            begin = end + 1;
            end = begin;
            p = root;
          } else {
            end++;
          }
        } else {
          sb.append(text.charAt(begin));
          begin++;
          end = begin;
          p = root;
        }
      } else {
        sb.append(text.charAt(begin));
        begin++;
        end = begin;
        p = root;
      }
    }

    return sb.toString();
  }
}
