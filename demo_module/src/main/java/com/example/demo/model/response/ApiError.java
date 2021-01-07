package com.example.demo.model.response;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by HoYoung on 2021/01/07.
 */
@Getter
public class ApiResponseError {
  private final HttpStatus status;
  private final String message;
  private final List<String> errors;

  public ApiResponseError(HttpStatus status, String message, List<String> errors) {
    super();
    this.status = status;
    this.message = message;
    this.errors = errors;
  }

  public ApiResponseError(HttpStatus status, String message, String error) {
    super();
    this.status = status;
    this.message = message;
    errors = Collections.singletonList(error);
  }

}
