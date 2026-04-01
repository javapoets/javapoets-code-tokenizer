package javapoets.tokenizer.obfuscation;

import java.util.HashMap;
import java.util.Map;

class Scope {

    private final Map<String, String> symbols = new HashMap<>();

    public boolean contains(String name) {
        return symbols.containsKey(name);
    }

    public String get(String name) {
        return symbols.get(name);
    }

    public void put(String original, String obfuscated) {
        symbols.put(original, obfuscated);
    }
}