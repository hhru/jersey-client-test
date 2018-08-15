package ru.hh.jersey.test.jaxb;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.List;
import java.util.Map;

public class MultivaluedMapAdapter<K, V> extends XmlAdapter<AdaptedMultivaluedMap<K, V>, MultivaluedMap<K, V>> {

  @Override
  public MultivaluedMap<K, V> unmarshal(AdaptedMultivaluedMap<K, V> adaptedMap) {
    MultivaluedMap<K, V> map = new MultivaluedHashMap<>();
    for(MultivaluedEntry<K, V> entry : adaptedMap.entry) {
      map.put(entry.key, entry.value);
    }
    return map;
  }

  @Override
  public AdaptedMultivaluedMap<K, V> marshal(MultivaluedMap<K, V> map) {
    AdaptedMultivaluedMap<K, V> adaptedMap = new AdaptedMultivaluedMap<>();
    for (Map.Entry<K, List<V>> mapEntry : map.entrySet()) {
      MultivaluedEntry<K, V> entry = new MultivaluedEntry<>();
      entry.key = mapEntry.getKey();
      entry.value = mapEntry.getValue();
      adaptedMap.entry.add(entry);
    }
    return adaptedMap;
  }
}
