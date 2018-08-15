package ru.hh.jersey.test;

import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.Response.Status.MOVED_PERMANENTLY;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JerseyClientTestTest extends JerseyClientTest {

  @Override
  protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
    return new InMemoryTestContainerFactory();
  }

  @Test
  public void testReturnContent() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
      ExpectedResponse.builder().content(expectAnswer).mediaType("text/plain").build());
    WebTarget resource = getWebTarget(getBaseUri());

    String actualAnswer = resource.path("/test").request().get(String.class);

    assertEquals(expectAnswer, actualAnswer);
  }

  @Test
  public void testReturnMediaType() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(), ExpectedResponse.builder().content(expectAnswer).build());
    WebTarget resource = getWebTarget(getBaseUri());

    Entity actualAnswer = resource.path("/test").request().get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testFindResponseByQueryParams() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").addQueryParam("testParam", "testValue").build(),
      ExpectedResponse.builder().content(expectAnswer).build());
    WebTarget resource = getWebTarget(getBaseUri());

    Entity actualAnswer = resource.path("/test").queryParam("testParam", "testValue").request().get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testGetActualRequest() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    final RequestMapping requestMapping = RequestMapping.builder(HttpMethod.PUT, "/test").addQueryParam("testParam", "testValue").build();
    setServerAnswer(requestMapping, ExpectedResponse.builder().content(expectAnswer).build());
    WebTarget resource = getWebTarget(getBaseUri());

    resource.path("/test").queryParam("testParam", "testValue")
      .request()
      .header("h1", "v1.1").header("h1", "v1.2")
      .put(javax.ws.rs.client.Entity.xml("<test val=\"1\">"));
    resource.path("/test").queryParam("testParam", "testValue")
      .request()
      .header("h2", "v2.1").header("h2", "v2.2").header("h2", "v2.3")
      .put(javax.ws.rs.client.Entity.xml("<test val=\"2\">"));

    List<ActualRequest> actualRequests = getActualRequests(requestMapping);

    assertEquals(2, actualRequests.size());
    final ActualRequest actualRequest1 = actualRequests.get(0);
    assertEquals("<test val=\"1\">", actualRequest1.getContent());

    final List<String> header1 = actualRequest1.getHeaders().get("h1");
    assertEquals(2, header1.size());
    assertEquals("v1.1", header1.get(0));

    final ActualRequest actualRequest2 = actualRequests.get(1);
    assertEquals("<test val=\"2\">", actualRequest2.getContent());

    final List<String> header2 = actualRequest2.getHeaders().get("h2");
    assertEquals(3, header2.size());
    assertEquals("v2.1", header2.get(0));
  }

  @Test
  public void testReturn404StatusWhenQueryParamsNotPresentInResponse() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").addQueryParam("testParam", "testValue").build(),
      ExpectedResponse.builder().content(expectAnswer).mediaType("text/plain").build());
    WebTarget resource = getWebTarget(getBaseUri());

    Response response = resource.path("/test").request().get(Response.class);
    assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  public void testReturn404StatusWhenMakeRequestWithUnexpectedHttpMethod() {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    setServerAnswer(RequestMapping.builder(HttpMethod.PUT, "/test").build(),
      ExpectedResponse.builder().content(expectAnswer).mediaType("text/plain").build());

    WebTarget resource = getWebTarget(getBaseUri());

    Response response = resource.path("/test").request().get(Response.class);
    assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  public void testReturnStatus() {
    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
      ExpectedResponse.builder().status(MOVED_PERMANENTLY).mediaType("text/plain").build());
    WebTarget resource = getWebTarget(getBaseUri());

    final Response response = resource.path("/test").request().get(Response.class);

    assertEquals(MOVED_PERMANENTLY.getStatusCode(), response.getStatus());
  }

  @Test
  public void testReturnHeaders() {
    MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
    headers.put("headerName", Arrays.asList("headerValue1", "headerValue2"));

    setServerAnswer(RequestMapping.builder(HttpMethod.GET, "/test").build(),
      ExpectedResponse.builder().status(Response.Status.OK)
        .addHeaders(headers).mediaType("text/plain").build());

    Response response = client().target(getBaseUri()).path("/test").request().get();

    assertTrue(response.getHeaderString("headerName").contains("headerValue1,headerValue2"));
  }

  private WebTarget getWebTarget(URI uri) {
    return client().target(uri);
  }
}
