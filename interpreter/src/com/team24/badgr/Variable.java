package com.team24.badgr;

import java.util.HashMap;
import java.util.Map;

class Variable {
    public static final Map<String, Object> varTypes = new HashMap<>();

    static void define(String name, Object value) {
        varTypes.put(name, value);
    }

    static void remove(String name) {
        varTypes.remove(name);
    }
}