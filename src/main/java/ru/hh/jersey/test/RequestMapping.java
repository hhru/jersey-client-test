package ru.hh.jersey.test;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import org.apache.commons.lang.StringUtils;
import javax.annotation.concurrent.NotThreadSafe;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class RequestMapping {
  private String path;
  private HttpMethod httpMethod;
  private MultivaluedMap<String, String> params = new StringKeyIgnoreCaseMultivaluedMap<String>();

  private RequestMapping(HttpMethod httpMethod, String path) {
    this.httpMethod = httpMethod;
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public MultivaluedMap<String, String> getParams() {
    return params;
  }

  public static RequestMappingBuilder builder(HttpMethod httpMethod, String path) {
    return new RequestMappingBuilder(httpMethod, path);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    RequestMapping that = (RequestMapping) o;

    if (params != null ? !params.equals(that.params) : that.params != null) {
      return false;
    }
    if (path != null ? !path.equals(that.path) : that.path != null) {
      return false;
    }
    if (httpMethod != null ? !httpMethod.equals(that.httpMethod) : that.httpMethod != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = path != null ? path.hashCode() : 0;
    result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 0);
    result = 31 * result + (params != null ? params.hashCode() : 0);
    return result;
  }

  @NotThreadSafe
  public static class RequestMappingBuilder {
    private RequestMapping requestMapping;

    private RequestMappingBuilder(HttpMethod httpMethod, String path) {
      if (httpMethod == null) {
        throw new IllegalStateException("Http method should not be null");
      }

      if (StringUtils.isBlank(path)){
        throw new IllegalStateException("path should not be empty");
      }

      requestMapping = new RequestMapping(httpMethod, path);
    }

    public RequestMapping build() {
      return requestMapping;
    }

    public RequestMappingBuilder addQueryParam(String name, String value) {
      requestMapping.params.putSingle(name, value);
      return this;
    }

    public RequestMappingBuilder addQueryParam(String name, List<String> values) {
      requestMapping.params.put(name, values);
      return this;
    }

    public RequestMappingBuilder addQueryParams(MultivaluedMap<String, String> queryParameters) {
      if (queryParameters == null) {
        return this;
      }

      requestMapping.params.putAll(queryParameters);

      return this;
    }
  }
}
