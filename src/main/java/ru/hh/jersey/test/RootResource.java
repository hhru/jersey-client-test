package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
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
  private Map<RequestMapping, ExpectedResponse> pathContextMap = new HashMap<RequestMapping, ExpectedResponse>();

  private void setHeaders(RequestMapping requestMapping, MultivaluedMap<String, String> responseHeaders) {
    ExpectedResponse expectedResponse = getPathContext(requestMapping);
    expectedResponse.setResponseHeaders(responseHeaders);
  }

  private void setEntity(RequestMapping requestMapping, String entity) {
    ExpectedResponse expectedResponse = getPathContext(requestMapping);
    expectedResponse.setEntity(entity);
  }

  private void setResponseStatus(RequestMapping requestMapping, ClientResponse.Status status) {
    ExpectedResponse expectedResponse = getPathContext(requestMapping);
    expectedResponse.setStatus(status);
  }

  private ExpectedResponse getPathContext(RequestMapping requestMapping) {
    ExpectedResponse expectedResponse;
    expectedResponse = pathContextMap.get(requestMapping);

    if (expectedResponse == null) {
      expectedResponse = new ExpectedResponse();
      pathContextMap.put(requestMapping, expectedResponse);
    }

    return expectedResponse;
  }

  @GET
  @Path("{path:.+}")
  @Produces({ "application/xml" })
  public Response content(@PathParam("path") String path, @Context
      UriInfo uriInfo) {
    return getResponseBuilder("/" + path, uriInfo.getQueryParameters()).build();
  }

  @POST
  @Path("{path:.+}")
  @Produces({ "application/xml" })
  public Response contentPost(@PathParam("path") String path) {
    return getResponseBuilder("/" + path, null).build();
  }

  private Response.ResponseBuilder getResponseBuilder(String path, MultivaluedMap<String, String> queryParameters) {
    ExpectedResponse expectedResponse = pathContextMap.get(new RequestMapping(path, queryParameters));
    if (expectedResponse == null) {
      return Response.status(Response.Status.NOT_FOUND);
    }

    ClientResponse.Status actualStatus = expectedResponse.getStatus();
    Response.ResponseBuilder responseBuilder = Response.status(actualStatus);

    MultivaluedMap<String, String> responseHeaders = expectedResponse.getResponseHeaders();
    if (responseHeaders != null) {
      for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
        for (String headerValue : entry.getValue()) {
          responseBuilder.header(entry.getKey(), headerValue);
        }
      }
    }

    responseBuilder.entity(expectedResponse.getEntity());

    return responseBuilder;
  }

  @POST
  @Path("/setParams")
  @Produces({ "text/plain" })
  public Response setParameters(@Context
      UriInfo ui) throws UnsupportedEncodingException {
    MultivaluedMap<String, String> queryParameters = ui.getQueryParameters();

    RequestMapping requestMapping = generateRequestMapping(queryParameters);

    List<String> headers = queryParameters.get("header");
    if (headers != null && !headers.isEmpty()) {
      setHeaders(requestMapping, parseMultivaluedMapFromQueryParameter(headers));
    }

    String status = queryParameters.getFirst("status");
    if (StringUtils.isNotBlank(status) && StringUtils.isNumeric(status)) {
      setResponseStatus(requestMapping, ClientResponse.Status.fromStatusCode(NumberUtils.toInt(status, 500)));
    }

    String entity = queryParameters.getFirst("entity");
    if (StringUtils.isNotBlank(entity)) {
      setEntity(requestMapping, URLDecoder.decode(entity, "UTF-8"));
    }

    return Response.status(Response.Status.OK).entity("ok").build();
  }

  private RequestMapping generateRequestMapping(MultivaluedMap<String, String> queryParameters) {
    String path = queryParameters.getFirst("path");
    if (StringUtils.isBlank(path)) {
      path = "/";
    }

    MultivaluedMap<String, String> requestParams = null;
    List<String> params = queryParameters.get("queryParams");
    if (params != null && !params.isEmpty()) {
      requestParams = parseMultivaluedMapFromQueryParameter(params);
    }

    return new RequestMapping(path, requestParams);
  }

  private MultivaluedMap<String, String> parseMultivaluedMapFromQueryParameter(List<String> queryParameterValues) {
    MultivaluedMap<String, String> result = new StringKeyIgnoreCaseMultivaluedMap<String>();
    for (String queryParameterValue : queryParameterValues) {
      String[] queryParameterValueParts = queryParameterValue.split(":");
      String mapKey = queryParameterValueParts[0];
      String mapValue = queryParameterValueParts[1];
      result.add(mapKey, mapValue);
    }
    return result;
  }
}
