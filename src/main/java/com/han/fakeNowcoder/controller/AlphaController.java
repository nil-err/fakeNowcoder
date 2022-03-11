package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.service.AlphaService;
import com.han.fakeNowcoder.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

  @Autowired private AlphaService alphaService;

  @RequestMapping("/hello")
  @ResponseBody
  public String sayHello() {
    return "Hello Spring Boot!!!";
  }

  @RequestMapping("/data")
  @ResponseBody
  public String getData() {
    return alphaService.find();
  }

  @RequestMapping("/http")
  public void http(HttpServletRequest request, HttpServletResponse response) {
    System.out.println(request.getMethod());
    System.out.println(request.getServletPath());
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      String val = request.getHeader(name);
      System.out.println(name + " " + val);
    }
    System.out.println(request.getParameter("code"));
    response.setContentType("text/html;charset=utf-8");
    try (PrintWriter writer = response.getWriter()) {
      writer.write("<h1>nowcoder</h1>");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // 请求参数

  @RequestMapping(path = "/student", method = RequestMethod.GET)
  @ResponseBody
  public String getStudents(
      @RequestParam(name = "current", required = false, defaultValue = "1") int current,
      @RequestParam(name = "limit", required = false, defaultValue = "20") int limit) {
    System.out.println(current);
    System.out.println(limit);
    return "getStudents";
  }

  // 使用路径参数

  @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
  @ResponseBody
  public String getStudent(@PathVariable("id") int id) {
    System.out.println(id);
    return "getStudent";
  }

  // 接收数据

  @RequestMapping(path = "/student", method = RequestMethod.POST)
  @ResponseBody
  public String inputStudent(String name, int age) {
    System.out.println(name);
    System.out.println(age);
    return "success!";
  }

  // 使用模板引擎返回

  @RequestMapping(path = "/teacher", method = RequestMethod.GET)
  public ModelAndView getTeacher() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.addObject("name", "Wan");
    modelAndView.addObject("age", 24);
    modelAndView.setViewName("/demo/view");
    return modelAndView;
  }

  @RequestMapping(path = "/school", method = RequestMethod.GET)
  public String getTeacher(Model model) {
    model.addAttribute("name", "Wan");
    model.addAttribute("age", 90);
    return "/demo/view";
  }

  // 响应Json数据

  @RequestMapping(path = "/emp", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> getEmp() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三");
    map.put("age", "24");
    map.put("school", "bupt");
    return map;
  }

  @RequestMapping(path = "/emps", method = RequestMethod.GET)
  @ResponseBody
  public List<Map<String, Object>> getEmps() {
    List<Map<String, Object>> list = new ArrayList<>();
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三");
    map.put("age", "24");
    map.put("school", "bupt");
    list.add(map);
    map = new HashMap<>();
    map.put("name", "李四");
    map.put("age", "24");
    map.put("school", "bupt");
    list.add(map);
    map = new HashMap<>();
    map.put("name", "王五");
    map.put("age", "24");
    map.put("school", "bupt");
    list.add(map);
    return list;
  }

  @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
  @ResponseBody
  public String setCookie(HttpServletResponse response) {
    // 创建cookie
    Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
    // 设置作用域
    cookie.setPath("/nowcoder/alpha");
    // 设置生存时间(s)
    cookie.setMaxAge(600);
    // responde 添加Cookie
    response.addCookie(cookie);
    return "set cookie";
  }

  @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
  @ResponseBody
  public String getCookie(@CookieValue("code") String code) {
    System.out.println(code);
    return "get cookie";
  }

  @RequestMapping(path = "/session/set", method = RequestMethod.GET)
  @ResponseBody
  public String setSession(HttpSession session) {
    session.setAttribute("id", 1);
    session.setAttribute("name", "Test");

    return "set session";
  }

  @RequestMapping(path = "/session/get", method = RequestMethod.GET)
  @ResponseBody
  public String getSession(HttpSession session, HttpServletResponse response) {
    System.out.println();
    System.out.println(session.getAttribute("id"));
    System.out.println(session.getAttribute("name"));
    return "get session";
  }

  @RequestMapping(path = "/ajax", method = RequestMethod.POST)
  @ResponseBody
  public String testAjax(String name, int age) {
    System.out.println(name);
    System.out.println(age);
    return CommunityUtil.getJSONString(0, "操作成功！");
  }
}
