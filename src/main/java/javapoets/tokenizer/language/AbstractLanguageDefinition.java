package javapoets.tokenizer.language;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class AbstractLanguageDefinition implements LanguageDefinition {

    @Override
    public Set<String> booleanLiterals() {
        return Set.of("true", "false");
    }

    @Override
    public Set<String> nullLiterals() {
        return Set.of("null");
    }

    @Override
    public boolean supportsHashComments() {
        return false;
    }

    @Override
    public boolean supportsLineComments() {
        return true;
    }

    @Override
    public boolean supportsBlockComments() {
        return true;
    }

    @Override
    public boolean supportsCharLiterals() {
        return false;
    }

    @Override
    public boolean supportsBacktickStrings() {
        return false;
    }

    @Override
    public boolean isIdentifierStart(char ch) {
        return Character.isLetter(ch) || ch == '_' || ch == '$';
    }

    @Override
    public boolean isIdentifierPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '$';
    }

    protected List<String> sortLongestFirst(Set<String> ops) {
        return ops
            .stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .toList();
    }
}