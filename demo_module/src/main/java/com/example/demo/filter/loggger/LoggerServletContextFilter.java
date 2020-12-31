package com.example.demo.filter.loggger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * Created by HoYoung on 2020/12/31.
 */
@Component
@Slf4j
public class LoggerServletContextFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if(!ObjectUtils.isEmpty(request.getContentType()) &&
         request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)
    ) {
      ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest)request);
      chain.doFilter(wrapper, response);
    } else {
      chain.doFilter(request, response);
    }

  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void destroy() {

  }

  @Slf4j
  static class ReadableRequestWrapper extends HttpServletRequestWrapper {
    private final Charset encoding = StandardCharsets.UTF_8;
    private byte[] rawData;
    private Map<String, String[]> params = new HashMap<>();

    public ReadableRequestWrapper(HttpServletRequest request) throws IOException {
      super(request);
      this.rawData = IOUtils.toByteArray(request.getInputStream());
    }

    @Override
    public String getParameter(String name) {
      String[] paramArray = getParameterValues(name);
      if (paramArray != null && paramArray.length > 0) {
        return paramArray[0];
      } else {
        return null;
      }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      return Collections.unmodifiableMap(params);
    }

    @Override
    public Enumeration<String> getParameterNames() {
      return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
      String[] result = null;
      String[] dummyParamValue = params.get(name);

      if (dummyParamValue != null) {
        result = new String[dummyParamValue.length];
        System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
      }
      return result;
    }

    public void setParameter(String name, String value) {
      String[] param = {value};
      setParameter(name, param);
    }

    public void setParameter(String name, String[] values) {
      params.put(name, values);
    }

    @Override
    public ServletInputStream getInputStream() {
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

      return new ServletInputStream() {
        @Override
        public boolean isFinished() {
          return false;
        }

        @Override
        public boolean isReady() {
          return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
          // Do nothing
        }

        public int read() {
          return byteArrayInputStream.read();
        }
      };
    }

    @Override
    public BufferedReader getReader() {
      return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }
  }
}


