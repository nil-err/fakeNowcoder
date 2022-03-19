package com.han.fakeNowcoder.util;

import java.io.IOException;

public class WKTests {
  public static void main(String[] args) {
    //
    String cmd =
        "D:/wkhtmltox/bin/wkhtmltoimage --quality 75 http://localhost:8080/nowcoder/index D:/wkhtmltox/wk-images/1.png";
    try {
      Runtime.getRuntime().exec(cmd);
      System.out.println("ok.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
