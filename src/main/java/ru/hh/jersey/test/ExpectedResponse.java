package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MultivaluedMap;

class ExpectedResponse {
  private MultivaluedMap<String, String> responseHeaders;
  private String entity;
  private ClientResponse.Status status = ClientResponse.Status.OK;
  private MultivaluedMap<String, String> queryParams;

  ExpectedResponse() { }

  ExpectedResponse(MultivaluedMap<String, String> responseHeaders, String entity, ClientResponse.Status status) {
    this.responseHeaders = responseHeaders;
    this.entity = entity;
    this.status = status;
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

  public MultivaluedMap<String, String> getQueryParams() {
    return queryParams;
  }

  public void setRequestParams(MultivaluedMap<String, String> queryParams) {
    this.queryParams = queryParams;
  }
}