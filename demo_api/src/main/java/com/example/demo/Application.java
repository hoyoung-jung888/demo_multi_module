package com.example.demo;

import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by HoYoung on 2020/12/29.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    Locale.setDefault(Locale.KOREA);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
