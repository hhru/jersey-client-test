package ru.hh.jersey.test;

import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;

import javax.annotation.concurrent.NotThreadSafe;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

public class ExpectedResponse {
  private MultivaluedMap<String, String> headers;
  private String content;
  private Response.Status status = Response.Status.OK;
  private String mediaType = "application/xml";

  ExpectedResponse() { }

  public MultivaluedMap<String, String> getHeaders() {
    return headers;
  }

  public String getContent() {
    return content;
  }

  public Response.Status getStatus() {
    return status;
  }

  public String getMediaType() {
    return mediaType;
  }

  public static ExpectedResponseBuilder builder() {
    return new ExpectedResponseBuilder();
  }

  @NotThreadSafe
  public static class ExpectedResponseBuilder {
    private ExpectedResponse expectedResponse;

    private ExpectedResponseBuilder() {
      expectedResponse = new ExpectedResponse();
    }

    public ExpectedResponse build(){
      return expectedResponse;
    }

    public ExpectedResponseBuilder content(String content) {
      expectedResponse.content = content;
      return this;
    }

    public ExpectedResponseBuilder status(Response.Status status) {
      expectedResponse.status = status;
      return this;
    }

    public ExpectedResponseBuilder mediaType(String mediaType){
      expectedResponse.mediaType = mediaType;
      return this;
    }

    public ExpectedResponseBuilder addHeader(String name, String value){
      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<>();
      }

      expectedResponse.headers.putSingle(name, value);

      return this;
    }

    public ExpectedResponseBuilder addHeader(String name, List<String> values){
      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<>();
      }

      expectedResponse.headers.put(name, values);

      return this;
    }

    public ExpectedResponseBuilder addHeaders(MultivaluedMap<String, String> headers){
      if (headers == null){
        return this;
      }

      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<>();
      }

      expectedResponse.headers.putAll(headers);

      return this;
    }
  }
}
