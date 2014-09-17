package ru.hh.jersey.test;

import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LinkedValuesMultivaluedMap<K,V> implements MultivaluedMap<K, V> {

  private Map<K,List<V>> internalMap = new HashMap<K, List<V>>();

  public void putSingle(K key, V value) {
    if (value == null)
      return;

    List<V> l = getList(key);
    l.clear();
    l.add(value);
  }

  public void add(K key, V value) {
    if (value == null)
      return;

    List<V> l = getList(key);
    l.add(value);
  }

  public V getFirst(K key) {
    List<V> values = get(key);
    if (values != null && values.size() > 0)
      return values.get(0);
    else
      return null;
  }

  @Override
  public int size() {
    return internalMap.size();
  }

  @Override
  public boolean isEmpty() {
    return internalMap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return internalMap.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return internalMap.containsValue(value);
  }

  @Override
  public List<V> get(Object key) {
    return internalMap.get(key);
  }

  @Override
  public List<V> put(K key, List<V> value) {
    return internalMap.put(key, value);
  }

  @Override
  public List<V> remove(Object key) {
    return internalMap.remove(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends List<V>> m) {
    internalMap.putAll(m);
  }

  @Override
  public void clear() {

  }

  @Override
  public Set<K> keySet() {
    return internalMap.keySet();
  }

  @Override
  public Collection<List<V>> values() {
    return internalMap.values();
  }

  @Override
  public Set<Entry<K, List<V>>> entrySet() {
    return internalMap.entrySet();
  }

  protected List<V> getList(K key) {
    List<V> l = get(key);
    if (l == null) {
      l = new LinkedList<V>();
      put(key, l);
    }
    return l;
  }
}
