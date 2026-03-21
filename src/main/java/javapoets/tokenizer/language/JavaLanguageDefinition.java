package javapoets.tokenizer.language;

import java.util.List;
import java.util.Set;
import javapoets.tokenizer.core.*;

public final class JavaLanguageDefinition extends AbstractLanguageDefinition {

     public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName())
            .append("{")
            .append(name())
            .append("}")
            .toString();
     }

    private static final Set<String> KEYWORDS = Set.of(
        "abstract","assert","boolean","break","byte","case","catch","char","class",
        "const","continue","default","do","double","else","enum","extends","final",
        "finally","float","for","goto","if","implements","import","instanceof","int",
        "interface","long","native","new","package","private","protected","public",
        "return","short","static","strictfp","super","switch","synchronized","this",
        "throw","throws","transient","try","void","volatile","while","record","sealed",
        "permits","non-sealed","var"
    );

    private static final Set<String> OPS = Set.of(
        ">>>=", ">>=", "<<=", ">>>", ">>", "<<",
        "++", "--", "==", "!=", ">=", "<=", "&&", "||",
        "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=",
        "->", "::",
        "+", "-", "*", "/", "%", "=",
        ">", "<", "!", "~", "&", "|", "^", "?",
        ":"
    );

    private static final Set<Character> PUNCT = Set.of(
        '(', ')', '{', '}', '[', ']', ';', ',', '.','@'
    );

    @Override
    public String name() {
        return "Java";
    }

    @Override
    public Set<String> keywords() {
        return KEYWORDS;
    }

    @Override
    public List<String> operators() {
        return sortLongestFirst(OPS);
    }

    @Override
    public Set<Character> punctuation() {
        return PUNCT;
    }

    @Override
    public boolean supportsCharLiterals() {
        return true;
    }
}