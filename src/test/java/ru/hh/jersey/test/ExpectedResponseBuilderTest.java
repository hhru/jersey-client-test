package ru.hh.jersey.test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExpectedResponseBuilderTest {
  @Test
  public void testDefaultResponse() throws Exception {
    final ExpectedResponse expectedResponse = ExpectedResponse.builder().build();

    assertNotNull(expectedResponse);
    assertNull(expectedResponse.getEntity());
    assertNull(expectedResponse.getHeaders());
    assertEquals("application/xml", expectedResponse.getMediaType());
    assertEquals(ClientResponse.Status.OK, expectedResponse.getStatus());
  }

  @Test
  public void testCommonParameters() throws Exception {
    final ExpectedResponse expectedResponse = ExpectedResponse.builder()
      .entity("test response")
      .mediaType("text/plain")
      .status(ClientResponse.Status.FORBIDDEN)
      .build();

    assertNotNull(expectedResponse);
    assertEquals("test response", expectedResponse.getEntity());
    assertEquals("text/plain", expectedResponse.getMediaType());
    assertEquals(ClientResponse.Status.FORBIDDEN, expectedResponse.getStatus());
    assertNull(expectedResponse.getHeaders());
  }

  @Test
  public void testHeaders() throws Exception {

    final StringKeyIgnoreCaseMultivaluedMap<String> headersFromMap = new StringKeyIgnoreCaseMultivaluedMap<String>();
    headersFromMap.add("fromMap", "fromMapValue1");
    headersFromMap.add("fromMap", "fromMapValue2");

    final ExpectedResponse expectedResponse = ExpectedResponse.builder()
      .addHeader("singleValueHeader", "value1")
      .addHeader("multiValueHeader", Arrays.asList("multiValue1", "multiValue2", "multiValue3"))
      .addHeaders(headersFromMap)
      .build();

    assertEquals(3, expectedResponse.getHeaders().size());

    final List<String> singleHeaderValues = expectedResponse.getHeaders().get("singleValueHeader");
    assertEquals(1, singleHeaderValues.size());
    assertTrue(singleHeaderValues.contains("value1"));

    final List<String> multiHeaderValues = expectedResponse.getHeaders().get("multiValueHeader");
    assertEquals(3, multiHeaderValues.size());
    assertTrue(multiHeaderValues.contains("multiValue1"));
    assertTrue(multiHeaderValues.contains("multiValue2"));
    assertTrue(multiHeaderValues.contains("multiValue3"));

    final List<String> fromMapHeaderValues = expectedResponse.getHeaders().get("fromMap");
    assertEquals(3, multiHeaderValues.size());
    assertTrue(fromMapHeaderValues.contains("fromMapValue1"));
    assertTrue(fromMapHeaderValues.contains("fromMapValue2"));
  }
}