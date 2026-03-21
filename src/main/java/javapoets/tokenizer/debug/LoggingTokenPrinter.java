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

        for (Token token : tokens) {
            /*
            log.debug(String.format(
                "%-18s %-12s (%d:%d)",
                token.type(),
                token.lexeme(),
                token.start().line(),
                token.start().column()
            ));
            */
            log.debug(token.toString());
        }
    }

    public static void printToken(Token token) {

        if (!log.isDebugEnabled()) return;

        /*
        log.debug(String.format(
            "%-18s %-12s (%d:%d)",
            token.type(),
            token.lexeme(),
            token.start().line(),
            token.start().column()
        ));
        */
        log.debug(token.toString());
    }

}