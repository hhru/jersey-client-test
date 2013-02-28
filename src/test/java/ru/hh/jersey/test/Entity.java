package ru.hh.jersey.test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entity")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Entity {

    private String val;

    @XmlAttribute(name = "val")
    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
