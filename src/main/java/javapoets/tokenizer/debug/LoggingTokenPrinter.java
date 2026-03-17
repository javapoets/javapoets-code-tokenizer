package javapoets.tokenizer.debug;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javapoets.tokenizer.core.Token;

public class LoggingTokenPrinter {

    private static final Logger log = LoggerFactory.getLogger(LoggingTokenPrinter.class);

    public static void print(List<Token> tokens) {
        if (!log.isDebugEnabled()) return;

        log.debug("=== Token Stream ===");

        for (Token t : tokens) {
            log.debug(String.format(
                "%-18s %-12s (%d:%d)",
                t.type(),
                t.lexeme(),
                t.start().line(),
                t.start().column()
            ));
        }
    }
}