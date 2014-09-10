package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import javax.annotation.concurrent.NotThreadSafe;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class ExpectedResponse {
  private MultivaluedMap<String, String> headers;
  private String entity;
  private ClientResponse.Status status = ClientResponse.Status.OK;
  private String mediaType = "application/xml";

  ExpectedResponse() { }

  public MultivaluedMap<String, String> getHeaders() {
    return headers;
  }

  public String getEntity() {
    return entity;
  }

  public ClientResponse.Status getStatus() {
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

    public ExpectedResponseBuilder entity(String entity) {
      expectedResponse.entity = entity;
      return this;
    }

    public ExpectedResponseBuilder status(ClientResponse.Status status) {
      expectedResponse.status = status;
      return this;
    }

    public ExpectedResponseBuilder mediaType(String mediaType){
      expectedResponse.mediaType = mediaType;
      return this;
    }

    public ExpectedResponseBuilder addHeader(String name, String value){
      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<String>();
      }

      expectedResponse.headers.putSingle(name, value);

      return this;
    }

    public ExpectedResponseBuilder addHeader(String name, List<String> values){
      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<String>();
      }

      expectedResponse.headers.put(name, values);

      return this;
    }

    public ExpectedResponseBuilder addHeaders(MultivaluedMap<String, String> headers){
      if (headers == null){
        return this;
      }

      if (expectedResponse.headers == null) {
        expectedResponse.headers = new StringKeyIgnoreCaseMultivaluedMap<String>();
      }

      expectedResponse.headers.putAll(headers);

      return this;
    }
  }
}
