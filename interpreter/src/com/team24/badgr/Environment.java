package com.team24.badgr;

import java.util.HashMap;
import java.util.Map;

class Environment {
  private final Map<String, Object> values = new HashMap<>();

  Object get(Token name) {
    if (values.containsKey(name.getText())) {
      return values.get(name.getText());
    }

    throw new RuntimeError(name,
        "Undefined variable '" + name.getText() + "'.");
  }

  void define(String name, Object value) {
    values.put(name, value);
  }
}