package ru.hh.jersey.test;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public abstract class JerseyClientTest extends JerseyTest {

  @Override
  protected Application configure() {
    return new ResourceConfig(RootResource.class);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content) {
    setServerAnswer(path, content, Response.Status.OK);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content) {
    setServerAnswer(path, queryParams, content, Response.Status.OK);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content, String mediaType) {
    setServerAnswer(path, null, content, Response.Status.OK, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, String mediaType) {
    setServerAnswer(path, queryParams, content, Response.Status.OK, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, Response.Status responseStatus, String mediaType) {
    setServerAnswer(path, queryParams, content, responseStatus, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content, Response.Status responseStatus) {
    setServerAnswer(path, null, content, responseStatus, null, "application/xml");
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, Response.Status responseStatus) {
    setServerAnswer(path, queryParams, content, responseStatus, null, "application/xml");
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(
    String path, MultivaluedMap<String, String> queryParams, String content, Response.Status responseStatus, MultivaluedMap<String, String> headers,
    String mediaType) {
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, path).addQueryParams(queryParams).build(),
      ExpectedResponse.builder()
        .content(content).status(responseStatus).addHeaders(headers).mediaType(mediaType).build());
  }

  protected List<ActualRequest> getActualRequests(RequestMapping requestMapping) {
    WebTarget resource = target().path("/getActualRequests");
    resource = fillRequestMappingPart(resource, requestMapping);
    final ActualRequestList actualRequestList = resource.request().accept(MediaType.APPLICATION_XML_TYPE).get(ActualRequestList.class);
    return actualRequestList.getActualRequests();
  }

  protected void setServerAnswer(RequestMapping requestMapping, ExpectedResponse expectedResponse) {
    WebTarget resource = client().target(getBaseUri()).path("/setParams");

    resource = fillRequestMappingPart(resource, requestMapping);

    try {
      resource = resource.queryParam("response.status", String.valueOf(expectedResponse.getStatus().getStatusCode()));

      if (StringUtils.isNotBlank(expectedResponse.getContent())) {
        resource = resource.queryParam("response.content", URLEncoder.encode(expectedResponse.getContent(), "UTF-8")
          .replace("+", "%20"));
      }

    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    if (expectedResponse.getHeaders() != null && !expectedResponse.getHeaders().isEmpty()) {
      resource = addMultivaluedMapsToRequest(expectedResponse.getHeaders(), "response.headers", resource);
    }

    if (StringUtils.isNotBlank(expectedResponse.getMediaType())) {
      resource = resource.queryParam("response.mediaType", expectedResponse.getMediaType());
    }

    resource.request()
      .accept(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(StringUtils.EMPTY));
  }

  private WebTarget fillRequestMappingPart(WebTarget resource, RequestMapping requestMapping) {
    WebTarget resourceWithRequestMapping = resource.queryParam("request.httpMethod", requestMapping.getHttpMethod().name());

    if (requestMapping.getParams() != null && !requestMapping.getParams().isEmpty()) {
      resourceWithRequestMapping = addMultivaluedMapsToRequest(requestMapping.getParams(),
        "request.queryParams", resourceWithRequestMapping);
    }

    try {
      resourceWithRequestMapping = resourceWithRequestMapping.queryParam("request.path", URLEncoder.encode(requestMapping.getPath(), "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return resourceWithRequestMapping;
  }

  private WebTarget addMultivaluedMapsToRequest(MultivaluedMap<String, String> mapWithValues, String multivaluedName, WebTarget resource) {
    for (Map.Entry<String, List<String>> multivaluedEntities : mapWithValues.entrySet()) {
      for (String multivaluedEntity : multivaluedEntities.getValue()) {
        resource = resource.queryParam(multivaluedName, multivaluedEntities.getKey() + "," + multivaluedEntity);
      }
    }
    return resource;
  }
}
