package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import com.sun.jersey.spi.resource.Singleton;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

@Path("/")
@Singleton
public class RootResource {
  private Map<RequestMapping, ExpectedResponse> pathContextMap = new HashMap<RequestMapping, ExpectedResponse>();
  private MultivaluedMap<RequestMapping, ActualRequest> requestsHistory = new LinkedValuesMultivaluedMap<RequestMapping, ActualRequest>();

  @GET
  @Path("{path:.+}")
  public Response content(@PathParam("path") String path, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
    return getResponseBuilder(HttpMethod.GET, "/" + path, uriInfo.getQueryParameters(), headers.getRequestHeaders(), null).build();
  }

  @POST
  @Path("{path:.+}")
  public Response contentPost(@PathParam("path") String path, @Context UriInfo uriInfo, @Context HttpHeaders headers, String content) {
    return getResponseBuilder(HttpMethod.POST, "/" + path, uriInfo.getQueryParameters(), headers.getRequestHeaders(), content).build();
  }

  @PUT
  @Path("{path:.+}")
  public Response contentPut(@PathParam("path") String path, @Context UriInfo uriInfo, @Context HttpHeaders headers, String content) {
    return getResponseBuilder(HttpMethod.PUT, "/" + path, uriInfo.getQueryParameters(), headers.getRequestHeaders(), content).build();
  }

  @DELETE
  @Path("{path:.+}")
  public Response contentDelete(@PathParam("path") String path, @Context UriInfo uriInfo, @Context HttpHeaders headers, String content) {
    return getResponseBuilder(HttpMethod.DELETE, "/" + path, uriInfo.getQueryParameters(), headers.getRequestHeaders(), content).build();
  }

  private Response.ResponseBuilder getResponseBuilder(HttpMethod httpMethod,
                                                      String path,
                                                      MultivaluedMap<String, String> queryParameters,
                                                      MultivaluedMap<String, String> headers,
                                                      String mappedContent) {
    String content = StringUtils.isBlank(mappedContent) ? null : mappedContent;
    final RequestMapping requestMapping = RequestMapping.builder(httpMethod, path)
      .addQueryParams(queryParameters)
      .build();
    requestsHistory.add(requestMapping, new ActualRequest(content, headers));

    ExpectedResponse expectedResponse = pathContextMap.get(requestMapping);
    if (expectedResponse == null) {
      return Response.status(Response.Status.NOT_FOUND);
    }

    ClientResponse.Status actualStatus = expectedResponse.getStatus();
    Response.ResponseBuilder responseBuilder = Response.status(actualStatus);

    MultivaluedMap<String, String> responseHeaders = expectedResponse.getHeaders();
    if (responseHeaders != null) {
      for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
        for (String headerValue : entry.getValue()) {
          responseBuilder.header(entry.getKey(), headerValue);
        }
      }
    }

    responseBuilder.entity(expectedResponse.getContent());
    responseBuilder.type(expectedResponse.getMediaType());

    return responseBuilder;
  }

  @POST
  @Path("/setParams")
  @Produces({"text/plain"})
  public Response setParameters(@Context UriInfo ui) throws UnsupportedEncodingException {
    MultivaluedMap<String, String> queryParameters = ui.getQueryParameters();

    RequestMapping requestMapping = generateRequestMapping(queryParameters);
    ExpectedResponse.ExpectedResponseBuilder expectedResponseBuilder = ExpectedResponse.builder();

    List<String> headers = queryParameters.get("response.headers");
    if (headers != null && !headers.isEmpty()) {
      expectedResponseBuilder.addHeaders(parseMultivaluedMapFromQueryParameter(headers));
    }

    String status = queryParameters.getFirst("response.status");
    if (StringUtils.isNotBlank(status) && StringUtils.isNumeric(status)) {
      expectedResponseBuilder.status(ClientResponse.Status.fromStatusCode(NumberUtils.toInt(status, 500)));
    }

    String entity = queryParameters.getFirst("response.content");
    if (StringUtils.isNotBlank(entity)) {
      expectedResponseBuilder.content(URLDecoder.decode(entity, "UTF-8"));
    }

    String responseMediaType = queryParameters.getFirst("response.mediaType");
    if (StringUtils.isNotBlank(responseMediaType)) {
      expectedResponseBuilder.mediaType(responseMediaType.trim());
    }

    pathContextMap.put(requestMapping, expectedResponseBuilder.build());

    return Response.status(Response.Status.OK).entity("ok").build();
  }

  @GET
  @Path("/getActualRequests")
  @Produces({"application/xml"})
  public Response getRequest(@Context UriInfo ui) throws UnsupportedEncodingException {
    MultivaluedMap<String, String> queryParameters = ui.getQueryParameters();

    RequestMapping requestMapping = generateRequestMapping(queryParameters);
    final List<ActualRequest> actualRequests = requestsHistory.get(requestMapping);

    return Response.status(Response.Status.OK).entity(new ActualRequestList(actualRequests)).build();
  }

  private RequestMapping generateRequestMapping(MultivaluedMap<String, String> queryParameters) {
    HttpMethod httpMethod;
    if (StringUtils.isBlank(queryParameters.getFirst("request.httpMethod"))) {
      httpMethod = HttpMethod.GET;
    } else {
      httpMethod = HttpMethod.valueOf(queryParameters.getFirst("request.httpMethod").toUpperCase());
    }

    String path = queryParameters.getFirst("request.path");
    if (StringUtils.isBlank(path)) {
      path = "/";
    }

    MultivaluedMap<String, String> requestParams = null;
    List<String> params = queryParameters.get("request.queryParams");
    if (params != null && !params.isEmpty()) {
      requestParams = parseMultivaluedMapFromQueryParameter(params);
    }

    final RequestMapping.RequestMappingBuilder requestMappingBuilder = RequestMapping.builder(httpMethod, path).addQueryParams(requestParams);

    return requestMappingBuilder.build();
  }

  private MultivaluedMap<String, String> parseMultivaluedMapFromQueryParameter(List<String> queryParameterValues) {
    MultivaluedMap<String, String> result = new StringKeyIgnoreCaseMultivaluedMap<String>();
    for (String queryParameterValue : queryParameterValues) {
      String[] queryParameterValueParts = queryParameterValue.split(",");
      String mapKey = queryParameterValueParts[0];
      String mapValue = queryParameterValueParts[1];
      result.add(mapKey, mapValue);
    }
    return result;
  }
}
