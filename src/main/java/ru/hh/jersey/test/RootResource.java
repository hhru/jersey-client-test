package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.resource.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

@Path("/")
@Singleton
public class RootResource {
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
}
