package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MultivaluedMap;

class ExpectedResponse {
  private MultivaluedMap<String, String> responseHeaders;
  private String entity;
  private ClientResponse.Status status = ClientResponse.Status.OK;
  private String mediaType = "application/xml";

  ExpectedResponse() { }

  ExpectedResponse(MultivaluedMap<String, String> responseHeaders, String entity, ClientResponse.Status status) {
    this.responseHeaders = responseHeaders;
    this.entity = entity;
    this.status = status;
    this.mediaType = mediaType;
  }

  public MultivaluedMap<String, String> getResponseHeaders() {
    return responseHeaders;
  }

  public void setResponseHeaders(MultivaluedMap<String, String> responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  public String getEntity() {
    return entity;
  }

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public ClientResponse.Status getStatus() {
    return status;
  }

  public void setStatus(ClientResponse.Status status) {
    this.status = status;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }
}
