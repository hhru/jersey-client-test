package ru.hh.jersey.test;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;
import org.junit.Test;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestMappingBuilderTest {
  @Test
  public void testCreateRequestMappingWithoutOptionalProperties() throws Exception {
    final RequestMapping requestMapping = RequestMapping.builder(HttpMethod.PUT, "test/path").build();

    assertEquals(HttpMethod.PUT, requestMapping.getHttpMethod());
    assertEquals("test/path", requestMapping.getPath());
  }

  @Test
  public void shouldCreateRequestMappingWhenAddNullQueryParams() throws Exception {
    final RequestMapping requestMapping = RequestMapping.builder(HttpMethod.PUT, "test/path")
      .addQueryParam("name", "value")
      .addQueryParams(null)
      .build();

    assertEquals(HttpMethod.PUT, requestMapping.getHttpMethod());
    assertEquals("test/path", requestMapping.getPath());

    final MultivaluedMap<String, String> queryParams = requestMapping.getParams();
    assertEquals(1, queryParams.size());
    assertEquals("value", queryParams.get("name").get(0));
  }

  @Test
  public void testCreateRequestMappingWithQueryParams() throws Exception {

    final StringKeyIgnoreCaseMultivaluedMap<String> queryParametersFromMap = new StringKeyIgnoreCaseMultivaluedMap<String>();
    queryParametersFromMap.add("fromMap", "fromMapValue1");
    queryParametersFromMap.add("fromMap", "fromMapValue2");

    final RequestMapping requestMapping = RequestMapping.builder(HttpMethod.PUT, "test/path")
      .addQueryParam("singleValueParam", "value1")
      .addQueryParam("multiValueParam", Arrays.asList("multiValue1", "multiValue2", "multiValue3"))
      .addQueryParams(queryParametersFromMap)
      .build();

    assertEquals(HttpMethod.PUT, requestMapping.getHttpMethod());
    assertEquals("test/path", requestMapping.getPath());

    assertEquals(3, requestMapping.getParams().size());

    final List<String> singleParamValues = requestMapping.getParams().get("singleValueParam");
    assertEquals(1, singleParamValues.size());
    assertTrue(singleParamValues.contains("value1"));

    final List<String> multiParamValues = requestMapping.getParams().get("multiValueParam");
    assertEquals(3, multiParamValues.size());
    assertTrue(multiParamValues.contains("multiValue1"));
    assertTrue(multiParamValues.contains("multiValue2"));
    assertTrue(multiParamValues.contains("multiValue3"));

    final List<String> fromMapParamValues = requestMapping.getParams().get("fromMap");
    assertEquals(3, multiParamValues.size());
    assertTrue(fromMapParamValues.contains("fromMapValue1"));
    assertTrue(fromMapParamValues.contains("fromMapValue2"));
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenCreateBuilderWithoutHttpMethod() throws Exception {
    RequestMapping.builder(null, "test/path");
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenCreateBuilderWithoutPath() throws Exception {
    RequestMapping.builder(HttpMethod.PUT, null);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenCreateBuilderWithEmptyPath() throws Exception {
    RequestMapping.builder(HttpMethod.PUT, "  ");
  }
}
