package ru.hh.jersey.test.jaxb;

import ru.hh.jersey.test.LinkedValuesMultivaluedMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.List;
import java.util.Map;

public class MultivaluedMapAdapter<K, V> extends XmlAdapter<AdaptedMultivaluedMap<K, V>, MultivaluedMap<K, V>> {

  @Override
  public MultivaluedMap<K, V> unmarshal(AdaptedMultivaluedMap<K, V> adaptedMap) throws Exception {
    MultivaluedMap<K, V> map = new LinkedValuesMultivaluedMap<K, V>();
    for(MultivaluedEntry<K, V> entry : adaptedMap.entry) {
      map.put(entry.key, entry.value);
    }
    return map;
  }

  @Override
  public AdaptedMultivaluedMap<K, V> marshal(MultivaluedMap<K, V> map) throws Exception {
    AdaptedMultivaluedMap<K, V> adaptedMap = new AdaptedMultivaluedMap<K, V>();
    for (Map.Entry<K, List<V>> mapEntry : map.entrySet()) {
      MultivaluedEntry<K, V> entry = new MultivaluedEntry<K, V>();
      entry.key = mapEntry.getKey();
      entry.value = mapEntry.getValue();
      adaptedMap.entry.add(entry);
    }
    return adaptedMap;
  }
}
