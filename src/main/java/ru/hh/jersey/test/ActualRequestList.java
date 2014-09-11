package ru.hh.jersey.test;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "actualRequests")
public class ActualRequestList {
  @XmlElement(name = "actualRequest")
  private List<ActualRequest> actualRequests;

  public ActualRequestList() {
    //jaxb assumptions
  }

  public ActualRequestList(List<ActualRequest> actualRequests) {
    this.actualRequests = actualRequests;
  }

  public List<ActualRequest> getActualRequests() {
    return actualRequests;
  }
}
