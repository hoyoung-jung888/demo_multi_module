package com.example.demo.aop.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.stream.Collectors;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

/**
 * Created by HoYoung on 2020/12/30.
 */
@Component
@Aspect
public class LoggerRequestContextAspect {
  private static final Logger log = LoggerFactory.getLogger("com.example.logger");
  private final StringBuilder sb = new StringBuilder();
  private final String hyphen = StringUtils.rightPad("", 50, "-");

  @Pointcut("execution(* (@org.springframework.web.bind.annotation.RestController *).*(..))")
  public void loggerPointCut() {}

  @Around("loggerPointCut()")
  public Object task(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    StopWatch sw = new StopWatch();

    String target = proceedingJoinPoint.getSignature().getDeclaringTypeName()
        .concat(proceedingJoinPoint.getSignature().getName());

    HttpServletRequestWrapper request = new HttpServletRequestWrapper(
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());

    try {
      String requestTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      sb.append(hyphen).append(System.lineSeparator());
      sb.append(this.logHeaderMessage("Target", target));
      sb.append(this.logHeaderMessage("Url", request.getRequestURL()));
      sb.append(this.logHeaderMessage("Method", request.getMethod()));
      sb.append(this.logHeaderMessage("Request Time", requestTime));
      sb.append(this.logHeaderMessage("Request IP", this.getClientIP(request)));
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String keyName = headerNames.nextElement();
        sb.append(this.logHeaderMessage(keyName, request.getHeader(keyName)));
      }
      sb.append(this.logParameterMessage("Parameter Info",""));

      Enumeration<String> parameterNames = request.getParameterNames();
      if(!ObjectUtils.isEmpty(request.getContentType()) &&
          request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
        MultipartHttpServletRequest multipartHttpServletRequest = new StandardMultipartHttpServletRequest(request);
        parameterNames = multipartHttpServletRequest.getParameterNames();
      } else if(!ObjectUtils.isEmpty(request.getContentType()) &&
          request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
        String jsonString = new BufferedReader(
            new InputStreamReader(
                getInputStream(IOUtils.toByteArray(request.getInputStream())), StandardCharsets.UTF_8))
            .lines().collect(Collectors.joining(System.lineSeparator()));
        sb.append(this.logParameterMessage("Json", new ObjectMapper().readTree(jsonString)));
      }

      if(!ObjectUtils.isEmpty(parameterNames)) {
        while (parameterNames.hasMoreElements()) {
          String keyName = parameterNames.nextElement();
          sb.append(this.logParameterMessage(keyName, request.getParameter(keyName)));
        }
      }
      log.info("{}", sb.toString());
      sb.setLength(0);

      sw.start();
      Object result = proceedingJoinPoint.proceed();
      sw.stop();

      log.info("--- [ExecutionTime] : {} (ms)", sw.getTotalTimeMillis());
      log.info("{}", hyphen);
      return result;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      throw throwable;
    }

  }

  private String getContentAsString(byte[] buf, int maxLength, String charsetName) {
    if (buf == null || buf.length == 0) return "";
    int length = Math.min(buf.length, 1000);
    try {
      return new String(buf, 0, length, charsetName);
    } catch (UnsupportedEncodingException ex) {
      return "Unsupported Encoding";
    }
  }

  private String logHeaderMessage(String type, Object obj) {
    return new StringBuilder()
        .append("--- ")
        .append("[")
        .append(StringUtils.leftPad(WordUtils.capitalizeFully(type, '-'), 15))
        .append("] : ")
        .append(obj)
        .append(System.lineSeparator())
        .toString();
  }

  private String logParameterMessage(String type, Object obj) {
    return new StringBuilder()
        .append("--- ")
        .append("[")
        .append(StringUtils.leftPad(type, 15))
        .append("] : ")
        .append(obj)
        .append(System.lineSeparator())
        .toString();
  }
  private String getClientIP(HttpServletRequest request) {
    String remoteAddr = "";

    if (request != null) {
      remoteAddr = request.getHeader("X-FORWARDED-FOR");
      if (remoteAddr == null || "".equals(remoteAddr)) {
        remoteAddr = request.getRemoteAddr();
      }
    }
    return remoteAddr;
  }

  private static ServletInputStream getInputStream( byte[] rawData) throws IOException {

    final ByteArrayInputStream bais = new ByteArrayInputStream( rawData);
    ServletInputStream sis = new ServletInputStream() {

      @Override
      public int read() throws IOException {
        return bais.read();
      }

      @Override
      public void setReadListener(ReadListener readListener) {
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public boolean isFinished() {
        return false;
      }
    };

    return sis;
  }
}
