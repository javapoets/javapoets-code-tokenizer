package javapoets.tokenizer.debug;

import java.util.List;
import javapoets.tokenizer.token.Token;

public class TokenPrinter {
    public static void print(List<Token> tokens) {
        for (Token t : tokens) {
            System.out.printf(
                "%-18s %-12s (%d:%d)%n",
                t.type(),
                t.lexeme(),
                t.start().line(),
                t.start().column()
            );
        }
    }
}