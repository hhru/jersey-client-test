package ru.hh.jersey.test;

import ru.hh.jersey.test.jaxb.MultivaluedMapAdapter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class ActualRequest {
  @XmlElement
  private String content;

  @XmlJavaTypeAdapter(MultivaluedMapAdapter.class)
  @XmlElement
  private MultivaluedMap<String, String> headers;

  public ActualRequest() {
    //jaxb assumptions
  }

  public ActualRequest(String content, MultivaluedMap<String, String> headers) {
    this.content = content;
    this.headers = headers;
  }

  public String getContent() {
    return content;
  }

  public MultivaluedMap<String, String> getHeaders() {
    return headers;
  }
}
