package ru.hh.jersey.test.jaxb;

import java.util.ArrayList;
import java.util.List;

public class AdaptedMultivaluedMap<K, V> {

  public List<MultivaluedEntry<K, V>> entry = new ArrayList<MultivaluedEntry<K, V>>();

}
