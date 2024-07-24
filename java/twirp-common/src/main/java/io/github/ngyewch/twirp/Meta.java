package io.github.ngyewch.twirp;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Meta {
  private final Map<String, String> meta = new LinkedHashMap<>();

  public Meta set(String key, String value) {
    meta.put(key, value);
    return this;
  }

  public Map<String, String> get() {
    if (meta.isEmpty()) {
      return null;
    }
    return Collections.unmodifiableMap(meta);
  }
}
