package ru.hh.jersey.test;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;

class RequestMapping {
  private String path;
  private MultivaluedMap<String, String> params;

  RequestMapping(String path, MultivaluedMap<String, String> params) {
    this.path = path.startsWith("/") ? path : "/" + path;
    this.params = params == null ? new StringKeyIgnoreCaseMultivaluedMap<String>() : params;
  }

  public String getPath() {
    return path;
  }

  public MultivaluedMap<String, String> getParams() {
    return params;
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

    return true;
  }

  @Override
  public int hashCode() {
    int result = path != null ? path.hashCode() : 0;
    result = 31 * result + (params != null ? params.hashCode() : 0);
    return result;
  }
}
