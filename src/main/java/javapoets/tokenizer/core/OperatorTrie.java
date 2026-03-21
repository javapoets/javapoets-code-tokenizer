package javapoets.tokenizer.core;

import java.util.HashMap;
import java.util.Map;

public class OperatorTrie {

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        boolean isOperator;
    }

    private final Node root = new Node();

    public OperatorTrie(Iterable<String> operators) {
        for (String op : operators) {
            insert(op);
        }
    }

    private void insert(String op) {
        Node node = root;
        for (char c : op.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new Node());
        }
        node.isOperator = true;
    }

    public String match(CharReader reader) {
        Node node = root;
        int length = 0;
        int maxLength = -1;

        while (true) {
            char c = reader.peek(length);
            Node next = node.children.get(c);
            if (next == null) break;

            node = next;
            length++;

            if (node.isOperator) {
                maxLength = length;
            }
        }

        if (maxLength == -1) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            sb.append(reader.advance());
        }

        return sb.toString();
    }
}