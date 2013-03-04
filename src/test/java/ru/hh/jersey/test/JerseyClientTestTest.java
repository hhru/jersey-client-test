package ru.hh.jersey.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;

public class JerseyClientTestTest extends JerseyClientTest {
  @Test
  public void testReturnContent() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer("/test", expectAnswer);
    WebResource resource = getWebResource(getBaseURI());

    String actualAnswer = resource.path("/test").get(String.class);

    assertEquals(expectAnswer, actualAnswer);
  }

  @Test
  public void testReturnMediaType() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer("/test", expectAnswer, "application/xml");
    WebResource resource = getWebResource(getBaseURI());

    Entity actualAnswer = resource.path("/test").get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testFindResponseByQueryParams() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    StringKeyIgnoreCaseMultivaluedMap<String> queryParams = new StringKeyIgnoreCaseMultivaluedMap<String>();
    queryParams.putSingle("testParam", "testValue");

    setServerAnswer("/test", queryParams, expectAnswer, "application/xml");

    WebResource resource = getWebResource(getBaseURI());

    Entity actualAnswer = resource.path("/test").queryParam("testParam", "testValue").get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test(expected = UniformInterfaceException.class)
  public void testReturn404StatusWhenQueryParamsNotPresentInResponse() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    StringKeyIgnoreCaseMultivaluedMap<String> queryParams = new StringKeyIgnoreCaseMultivaluedMap<String>();
    queryParams.putSingle("testParam", "testValue");

    setServerAnswer("/test", queryParams, expectAnswer);

    WebResource resource = getWebResource(getBaseURI());

    try {
      resource.path("/test").get(String.class);
    } catch (UniformInterfaceException e) {
      ClientResponse response = e.getResponse();
      assertEquals(404, response.getStatus());
      throw e;
    }

    fail();
  }

  @Test(expected = UniformInterfaceException.class)
  public void testReturnStatus() throws Exception {
    setServerAnswer("/test", "", 301);
    WebResource resource = getWebResource(getBaseURI());

    try {
      resource.path("/test").get(String.class);
    } catch (UniformInterfaceException e) {
      ClientResponse response = e.getResponse();
      assertEquals(301, response.getStatus());
      throw e;
    }
    fail();
  }

  @Test
  public void testReturnHeaders() throws Exception {
    MultivaluedMapImpl headers = new MultivaluedMapImpl();
    headers.put("headerName", Arrays.asList("headerValue1", "headerValue2"));
    setServerAnswer("/test", null, "", 200, headers, "text/plain");

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
