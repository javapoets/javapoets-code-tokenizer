package javapoets.tokenizer.language;

import java.util.List;
import java.util.Set;
//import javapoets.tokenizer.core.*;

public final class JavascriptLanguageDefinition extends AbstractLanguageDefinition {

    private static final Set<String> KEYWORDS = Set.of(
        "break","case","catch","class","const","continue","debugger","default","delete",
        "do","else","export","extends","finally","for","function","if","import","in",
        "instanceof","let","new","return","super","switch","this","throw","try",
        "typeof","var","void","while","with","yield","await","static"
    );

    private static final Set<String> OPS = Set.of(
        ">>>=", "===", "!==", ">>=", "<<=", "&&=", "||=", "??=",
        "=>", "?.", "??", "++", "--", "==", "!=", ">=", "<=", "&&", "||",
        "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "**", ">>", "<<", ">>>",
        "+", "-", "*", "/", "%", "=",
        ">", "<", "!", "~", "&", "|", "^", "?",
        ":"
    );

    private static final Set<Character> PUNCT = Set.of(
        '(', ')', '{', '}', '[', ']', ';', ',', '.'
    );

    @Override
    public String name() {
        //return "JavaScript";
        return "JS";
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
    public boolean supportsBacktickStrings() {
        return true;
    }

    @Override
    public boolean supportsCharLiterals() {
        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName())
            .append("{")
            .append(name())
            .append("}")
            .toString();
     }

}