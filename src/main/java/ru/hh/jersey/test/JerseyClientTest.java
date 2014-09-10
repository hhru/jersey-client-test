package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

public abstract class JerseyClientTest extends JerseyTest {
  // avoid accidental overriding in subclasses
  @Before
  public void setUpJerseyClientTest() throws Exception {
    super.setUp();
  }

  // avoid accidental overriding in subclasses
  @After
  public void tearDownJerseyClientTest() throws Exception {
    super.tearDown();
  }

  @Override
  public TestContainerFactory getTestContainerFactory() {
    return new GrizzlyWebTestContainerFactory();
  }

  @Override
  protected AppDescriptor configure() {
    return new WebAppDescriptor.Builder().initParam(WebComponent.RESOURCE_CONFIG_CLASS, ClassNamesResourceConfig.class.getName())
    .initParam(ClassNamesResourceConfig.PROPERTY_CLASSNAMES, RootResource.class.getName())
    .build();
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content) {
    setServerAnswer(path, content, 200);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content) {
    setServerAnswer(path, queryParams, content, 200);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content, String mediaType) {
    setServerAnswer(path, null, content, 200, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, String mediaType) {
    setServerAnswer(path, queryParams, content, 200, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, Integer statusCode, String mediaType) {
    setServerAnswer(path, queryParams, content, statusCode, null, mediaType);
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, String content, Integer statusCode) {
    setServerAnswer(path, null, content, statusCode, null, "application/xml");
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, Integer statusCode) {
    setServerAnswer(path, queryParams, content, statusCode, null, "application/xml");
  }

  /**
   * @deprecated use {@link #setServerAnswer(RequestMapping, ExpectedResponse)} instead
   */
  @Deprecated
  protected void setServerAnswer(
      String path, MultivaluedMap<String, String> queryParams, String content, Integer statusCode, MultivaluedMap<String, String> headers,
      String mediaType) {
      setServerAnswer(RequestMapping.builder(HttpMethod.GET, path).addQueryParams(queryParams).build(),
                      ExpectedResponse.builder()
                        .entity(content).status(ClientResponse.Status.fromStatusCode(statusCode)).addHeaders(headers).mediaType(mediaType).build());
  }

  protected void setServerAnswer(RequestMapping requestMapping, ExpectedResponse expectedResponse) {
    URI baseURI = getBaseURI();

    WebResource resource = client().resource(baseURI).path("/setParams");

    try {
      resource = resource.queryParam("response.status", String.valueOf(expectedResponse.getStatus().getStatusCode()))
        .queryParam("request.path", URLEncoder.encode(requestMapping.getPath(), "UTF-8"));

      if (StringUtils.isNotBlank(expectedResponse.getEntity())){
        resource = resource.queryParam("response.entity", URLEncoder.encode(expectedResponse.getEntity(), "UTF-8"));
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    if (expectedResponse.getHeaders() != null && !expectedResponse.getHeaders().isEmpty()) {
      resource = addMultivaluedMapsToRequest(expectedResponse.getHeaders(), "response.headers", resource);
    }

    if (requestMapping.getParams() != null && !requestMapping.getParams().isEmpty()) {
      resource = addMultivaluedMapsToRequest(requestMapping.getParams(), "request.queryParams", resource);
    }

    if (StringUtils.isNotBlank(expectedResponse.getMediaType())) {
      resource = resource.queryParam("response.mediaType", expectedResponse.getMediaType());
    }

    resource = resource.queryParam("request.httpMethod", requestMapping.getHttpMethod().name());

    resource.type("application/x-www-form-urlencoded").accept(MediaType.TEXT_PLAIN_TYPE).post(String.class);
  }

  private WebResource addMultivaluedMapsToRequest(MultivaluedMap<String, String> mapWithValues, String multivaluedName, WebResource resource) {
    for (Map.Entry<String, List<String>> multivaluedEntities : mapWithValues.entrySet()) {
      for (String multivaluedEntity : multivaluedEntities.getValue()) {
        resource = resource.queryParam(multivaluedName, multivaluedEntities.getKey() + ":" + multivaluedEntity);
      }
    }
    return resource;
  }
}
