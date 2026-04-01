package javapoets.tokenizer.obfuscation;

public class NameGenerator {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NameGenerator.class);

    private int counter = 0;

    public String next() {
        return toBase26(counter++);
    }

    private String toBase26(int num) {
        log.trace("toBase26("+num+")");

        StringBuilder sb = new StringBuilder();
        do {
            char c = (char) ('a' + (num % 26));
            sb.insert(0, c);
            num = num / 26 - 1;
        } while (num >= 0);

        return sb.toString();
    }
}