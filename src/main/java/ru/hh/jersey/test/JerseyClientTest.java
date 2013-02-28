package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.servlet.WebComponent;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.grizzly.web.GrizzlyWebTestContainerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
    .initParam(ClassNamesResourceConfig.PROPERTY_CLASSNAMES, TestResource.class.getName())
    .build();
  }

  protected void setServerAnswer(String path, String content) {
    setServerAnswer(path, content, 200);
  }

  protected void setServerAnswer(String path, String content, Integer status) {
    setServerAnswer(path, content, status, null);
  }

  protected void setServerAnswer(String path, String content, Integer status, MultivaluedMap<String, String> headers) {
    URI baseURI = getBaseURI();

    WebResource resource = client().resource(baseURI).path("/setParams");

    try {
      resource = resource.queryParam("status", status.toString()).queryParam("path", URLEncoder.encode(path, "UTF-8"))
        .queryParam("entity", URLEncoder.encode(content, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    if (headers != null) {
      for (Map.Entry<String, List<String>> header : headers.entrySet()) {
        for (String headerValue : header.getValue()) {
          resource = resource.queryParam("header", header.getKey() + ":" + headerValue);
        }
      }
    }

    resource.type("application/x-www-form-urlencoded").accept(MediaType.TEXT_PLAIN_TYPE).post(String.class);
  }

  @Path("/")
  @Singleton
  public static class TestResource {
    private Map<String, PathContext> pathContextMap = new HashMap<String, PathContext>();

    private void setHeaders(String path, MultivaluedMap<String, String> responseHeaders) {
      PathContext pathContext = getPathContext(path);
      pathContext.setResponseHeaders(responseHeaders);
    }

    private void setEntity(String path, String entity) {
      PathContext pathContext = getPathContext(path);
      pathContext.setEntity(entity);
    }

    private void setResponseStatus(String path, ClientResponse.Status status) {
      PathContext pathContext = getPathContext(path);
      pathContext.setStatus(status);
    }

    private PathContext getPathContext(String path) {
      PathContext pathContext;
      pathContext = pathContextMap.get(path);

      if (pathContext == null) {
        pathContext = new PathContext();
        pathContextMap.put(path, pathContext);
      }

      return pathContext;
    }

    @GET
    @Path("{path:.+}")
    @Produces({ "application/xml" })
    public Response content(@PathParam("path") String path) {
      return getResponseBuilder("/" + path).build();
    }

    @POST
    @Path("{path:.+}")
    @Produces({ "application/xml" })
    public Response contentPost(@PathParam("path") String path) {
      return getResponseBuilder("/" + path).build();
    }

    private Response.ResponseBuilder getResponseBuilder(String path) {
      PathContext pathContext = pathContextMap.get(path);
      if (pathContext == null) {
        return Response.status(Response.Status.NOT_FOUND);
      }

      ClientResponse.Status actualStatus = pathContext.getStatus();
      Response.ResponseBuilder responseBuilder = Response.status(actualStatus);

      MultivaluedMap<String, String> responseHeaders = pathContext.getResponseHeaders();
      if (responseHeaders != null) {
        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
          for (String headerValue : entry.getValue()) {
            responseBuilder.header(entry.getKey(), headerValue);
          }
        }
      }

      responseBuilder.entity(pathContext.getEntity());

      return responseBuilder;
    }

    @POST
    @Path("/setParams")
    @Produces({ "text/plain" })
    public Response setParameters(@Context
        UriInfo ui) throws UnsupportedEncodingException {
      MultivaluedMap<String, String> queryParameters = ui.getQueryParameters();

      String path = queryParameters.getFirst("path");
      if (StringUtils.isBlank(path)) {
        path = "/";
      }

      List<String> headers = queryParameters.get("header");
      if (headers != null) {
        MultivaluedMap<String, String> responseHeaders = new MultivaluedMapImpl();
        for (String header : headers) {
          String[] headerParts = header.split(":");
          String headerName = headerParts[0];
          String headerValue = headerParts[1];
          responseHeaders.add(headerName, headerValue);
        }
        setHeaders(path, responseHeaders);
      }

      String status = queryParameters.getFirst("status");
      if (StringUtils.isNotBlank(status) && StringUtils.isNumeric(status)) {
        setResponseStatus(path, ClientResponse.Status.fromStatusCode(NumberUtils.toInt(status, 500)));
      }

      String entity = queryParameters.getFirst("entity");
      if (StringUtils.isNotBlank(entity)) {
        setEntity(path, URLDecoder.decode(entity, "UTF-8"));
      }

      return Response.status(Response.Status.OK).entity("ok").build();
    }

    private class PathContext {
      private MultivaluedMap<String, String> responseHeaders;
      private String entity;
      private ClientResponse.Status status = ClientResponse.Status.OK;

      private PathContext() { }

      private PathContext(MultivaluedMap<String, String> responseHeaders, String entity, ClientResponse.Status status) {
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
    }
  }
}
