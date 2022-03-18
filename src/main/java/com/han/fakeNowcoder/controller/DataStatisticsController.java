package com.han.fakeNowcoder.controller;

import com.han.fakeNowcoder.service.DataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping(path = "/statistics")
public class DataStatisticsController {

  @Autowired private DataStatisticsService dataStatisticsService;

  @RequestMapping(
      path = "/page",
      method = {RequestMethod.GET, RequestMethod.POST})
  public String getDataStatisticsPage() {
    return "/site/admin/data";
  }

  @RequestMapping(path = "/uv", method = RequestMethod.POST)
  public String getUV(
      @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
      Model model) {
    long uv = dataStatisticsService.getUV(startDate, endDate);

    model.addAttribute("uv", uv);
    model.addAttribute("uvStartDate", startDate);
    model.addAttribute("uvEndDate", endDate);

    return "forward:/statistics/page";
  }

  @RequestMapping(path = "/dau", method = RequestMethod.POST)
  public String getDAU(
      @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
      Model model) {
    long dau = dataStatisticsService.getDAU(startDate, endDate);

    model.addAttribute("dau", dau);
    model.addAttribute("dauStartDate", startDate);
    model.addAttribute("dauEndDate", endDate);

    return "forward:/statistics/page";
  }
}
