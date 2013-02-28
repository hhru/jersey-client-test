package ru.hh.jersey.test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.net.URI;
import static junit.framework.Assert.assertEquals;
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

  private WebResource getWebResource(URI uri) {
    Client client = new Client();
    return client.resource(uri);
  }
}
