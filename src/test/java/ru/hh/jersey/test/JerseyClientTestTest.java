package ru.hh.jersey.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import java.net.URI;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.junit.Test;

public class JerseyClientTestTest extends JerseyClientTest {
  @Test
  public void testReturnContent() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
      + "<entity val=\"1\"/>";
    setServerAnswer("/test", expectAnswer);
    WebResource resource = getWebResource(getBaseURI());

    Entity actualAnswer = resource.path("/test").get(Entity.class);

    assertEquals("1", actualAnswer.getVal());
  }

  @Test
  public void testFindResponseByQueryParams() throws Exception {
    String expectAnswer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><entity val=\"1\"/>";

    StringKeyIgnoreCaseMultivaluedMap<String> queryParams = new StringKeyIgnoreCaseMultivaluedMap<String>();
    queryParams.putSingle("testParam", "testValue");

    setServerAnswer("/test", queryParams, expectAnswer);

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
      resource.path("/test").get(Entity.class);
    } catch (UniformInterfaceException e) {
      ClientResponse response = e.getResponse();
      assertEquals(404, response.getStatus());
      throw e;
    }

    fail();
  }

  private WebResource getWebResource(URI uri) {
    Client client = new Client();
    return client.resource(uri);
  }
}
