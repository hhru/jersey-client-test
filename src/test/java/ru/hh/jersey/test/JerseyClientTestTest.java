package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JerseyClientTestTest extends JerseyClientTest {
  @Test
  public void testReturnContent() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
                    ExpectedResponse.builder().entity(expectAnswer).mediaType("text/plain").build());
    WebResource resource = getWebResource(getBaseURI());

    String actualAnswer = resource.path("/test").get(String.class);

    assertEquals(expectAnswer, actualAnswer);
  }

  @Test
  public void testReturnMediaType() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(), ExpectedResponse.builder().entity(expectAnswer).build());
    WebResource resource = getWebResource(getBaseURI());

    Entity actualAnswer = resource.path("/test").get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testFindResponseByQueryParams() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").addQueryParam("testParam", "testValue").build(),
                    ExpectedResponse.builder().entity(expectAnswer).build());
    WebResource resource = getWebResource(getBaseURI());

    Entity actualAnswer = resource.path("/test").queryParam("testParam", "testValue").get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testReturn404StatusWhenQueryParamsNotPresentInResponse() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").addQueryParam("testParam", "testValue").build(),
                    ExpectedResponse.builder().entity(expectAnswer).mediaType("text/plain").build());
    WebResource resource = getWebResource(getBaseURI());

    ClientResponse response = resource.path("/test").get(ClientResponse.class);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testReturn404StatusWhenMakeRequestWithUnexpectedHttpMethod() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.PUT, "/test").build(),
                    ExpectedResponse.builder().entity(expectAnswer).mediaType("text/plain").build());

    WebResource resource = getWebResource(getBaseURI());

    ClientResponse response = resource.path("/test").get(ClientResponse.class);
    assertEquals(404, response.getStatus());
  }

  @Test
  public void testReturnStatus() throws Exception {
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
                    ExpectedResponse.builder().status(ClientResponse.Status.MOVED_PERMANENTLY).mediaType("text/plain").build());
    WebResource resource = getWebResource(getBaseURI());

    final ClientResponse response = resource.path("/test").get(ClientResponse.class);

    assertEquals(301, response.getStatus());
  }

  @Test
  public void testReturnHeaders() throws Exception {
    MultivaluedMapImpl headers = new MultivaluedMapImpl();
    headers.put("headerName", Arrays.asList("headerValue1", "headerValue2"));

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
                    ExpectedResponse.builder().status(ClientResponse.Status.OK).addHeaders(headers).mediaType("text/plain").build());

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    client().addFilter(new LoggingFilter(new PrintStream(byteArrayOutputStream)));
    client().resource(getBaseURI()).path("/test").get(String.class);

    String actualAnswer = byteArrayOutputStream.toString();
    assertTrue(actualAnswer.contains("headerName: headerValue2"));
    assertTrue(actualAnswer.contains("headerName: headerValue1"));
  }

  private WebResource getWebResource(URI uri) {
    return client().resource(uri);
  }
}
