package com.example.demo.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HoYoung on 2020/12/29.
 */
@RestController
@RequestMapping(value = "/order")
@Slf4j
public class OrderController {
  @GetMapping(value = "/{orderNumber}")
  public ResponseEntity<String> findOrder(@PathVariable Long orderNumber) {
    log.debug(">>>>>>>>> Controller findOrder");
    return ResponseEntity.ok("");
  }

  @PostMapping(value = "/{orderNumber}/2")
  public ResponseEntity<String> addOrder2(@PathVariable Long orderNumber, @RequestBody String r) {
    log.debug(">>>>>>>>> Controller addOrder");
    log.debug("JSON {}", r);
    return ResponseEntity.ok("");
  }

  @PostMapping(value = "/{orderNumber}")
  public ResponseEntity<String> addOrder(@PathVariable Long orderNumber,
  @RequestParam(required = false) MultipartFile file) {
    log.debug(">>>>>>>>> Controller addOrder");
    log.debug("{}", file);
    return ResponseEntity.ok("");
  }

  @PutMapping(value = "/{orderNumber}")
  public ResponseEntity<String> putOrder(@PathVariable Long orderNumber) {
    log.debug(">>>>>>>>> Controller addOrder");

    return ResponseEntity.ok("");
  }

  @DeleteMapping(value = "/{orderNumber}")
  public ResponseEntity<String> deleteOrder(@PathVariable Long orderNumber) {
    log.debug(">>>>>>>>> Controller addOrder");
    return ResponseEntity.ok("");
  }
}
