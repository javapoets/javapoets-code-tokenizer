package javapoets.tokenizer.core;

import java.util.Set;
import java.util.List;

public interface LanguageDefinition {
    
    String name();

    Set<String> keywords();
    Set<String> booleanLiterals();
    Set<String> nullLiterals();

    List<String> operators();     // ordered longest-first
    Set<Character> punctuation();

    boolean supportsHashComments();
    boolean supportsLineComments();
    boolean supportsBlockComments();

    boolean supportsCharLiterals();
    boolean supportsBacktickStrings();

    boolean isIdentifierStart(char ch);
    boolean isIdentifierPart(char ch);

    
}