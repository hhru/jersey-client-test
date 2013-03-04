package ru.hh.jersey.test;

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

  protected void setServerAnswer(String path, String content) {
    setServerAnswer(path, content, 200);
  }

  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content) {
    setServerAnswer(path, queryParams, content, 200);
  }

  protected void setServerAnswer(String path, String content, String mediaType) {
    setServerAnswer(path, null, content, 200, null, mediaType);
  }

  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, String mediaType) {
    setServerAnswer(path, queryParams, content, 200, null, mediaType);
  }

  protected void setServerAnswer(String path, String content, Integer status) {
    setServerAnswer(path, null, content, status, null, "text/plain");
  }

  protected void setServerAnswer(String path, MultivaluedMap<String, String> queryParams, String content, Integer status) {
    setServerAnswer(path, queryParams, content, status, null, "text/plain");
  }

  protected void setServerAnswer( String path, MultivaluedMap<String, String> queryParams, String content, Integer status, MultivaluedMap<String, String> headers, String responseMediaType) {
    URI baseURI = getBaseURI();

    WebResource resource = client().resource(baseURI).path("/setParams");

    try {
      resource = resource.queryParam("status", status.toString()).queryParam("path", URLEncoder.encode(path, "UTF-8"))
        .queryParam("entity", URLEncoder.encode(content, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    if (headers != null && !headers.isEmpty()) {
      resource = addMultivaluedMapsToRequest(headers, "header", resource);
    }

    if (queryParams != null && !queryParams.isEmpty()) {
      resource = addMultivaluedMapsToRequest(queryParams, "queryParams", resource);
    }

    if(StringUtils.isNotBlank(responseMediaType)){
      resource = resource.queryParam("mediaType", responseMediaType);
    }

    resource.type("application/x-www-form-urlencoded").accept(MediaType.TEXT_PLAIN_TYPE).post(String.class);
  }

  private WebResource addMultivaluedMapsToRequest(MultivaluedMap<String, String> mapWithValues, String multivaluedName, WebResource resource) {
    for (Map.Entry<String, List<String>> header : mapWithValues.entrySet()) {
      for (String headerValue : header.getValue()) {
        resource = resource.queryParam(multivaluedName, header.getKey() + ":" + headerValue);
      }
    }
    return resource;
  }
}
