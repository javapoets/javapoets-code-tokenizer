package javapoets.tokenizer.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SanityTest {

    @Test
    void should_run() {
        System.out.println("JavaTokenizerTest.should_run()");
        System.out.println("TEST IS RUNNING");
        assertThat(1 + 1).isEqualTo(2);
    }
}