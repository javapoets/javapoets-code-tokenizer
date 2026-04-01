package javapoets.tokenizer.obfuscation;

import java.util.ArrayDeque;
import java.util.Deque;

class ScopeStack {

    private final Deque<Scope> stack = new ArrayDeque<>();

    public void push() {
        stack.push(new Scope());
    }

    public void pop() {
        stack.pop();
    }

    public Scope current() {
        return stack.peek();
    }

    public String resolve(String name) {
        for (Scope scope : stack) {
            if (scope.contains(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    public void declare(String original, String obfuscated) {
        current().put(original, obfuscated);
    }
}