# Javapoets Code Tokenizer

A production-grade, extensible tokenizer and parser framework for Java and JavaScript, written in Java.

---

## Overview

**Javapoets Code Tokenizer** is a high-performance lexical analysis and parsing library designed for:

* Tokenizing source code into structured tokens
* Building Abstract Syntax Trees (AST)
* Supporting multiple programming languages (initially Java andd JavaScript)
* Serves as a foundation for compilers, linters, analyzers, and developer tools

This project emphasizes **clean architecture**, **extensibility**, and **production-grade design principles**.

---

## Features

* Language-agnostic tokenizer core
* Java and JavaScript language support
* LL-style `TokenStream` with lookahead
* Operator trie for efficient longest-match parsing
* Context-aware JavaScript regex handling
* Recursive descent parser with operator precedence
* Extensible AST model
* Clean separation of concerns (lexer, parser, AST)

---

## Architecture

```text
Source Code
   ↓
Tokenizer (Lexer)
   ↓
Token Stream
   ↓
Parser
   ↓
Abstract Syntax Tree (AST)
```

### Modules

```text
com.javapoets.tokenizer
├── core        # Tokenizer engine, TokenStream, utilities
├── language    # Java / JavaScript language definitions
├── parser      # Recursive descent parser
├── ast         # AST node definitions
```

---

## Installation

### Gradle

```gradle
dependencies {
    implementation 'com.javapoets:code-tokenizer:1.0.0'
}
```

### Maven

```xml
<dependency>
  <groupId>com.javapoets</groupId>
  <artifactId>code-tokenizer</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## Quick Start

### Tokenizing Code

```java
Tokenizer tokenizer = new Tokenizer(new JavaScriptLanguageDefinition());

List<Token> tokens = tokenizer.tokenize(
    "let x = 10 + 5 * 2;"
);

tokens.forEach(System.out::println);
```

---

### Parsing into AST

```java
Tokenizer tokenizer = new Tokenizer(new JavaScriptLanguageDefinition());
List<Token> tokens = tokenizer.tokenize("let x = 10 + 5 * 2;");

TokenStream stream = new TokenStream(tokens);
Parser parser = new Parser(stream);

List<Statement> ast = parser.parseProgram();

System.out.println(ast);
```

---

## Example AST

Input:

```javascript
let x = 10 + 5 * 2;
```

AST:

```text
VariableDeclaration
 ├── name: x
 └── initializer:
      BinaryExpression (+)
        ├── Literal(10)
        └── BinaryExpression (*)
             ├── Literal(5)
             └── Literal(2)
```

## Testing
```
$ gradle test
# gradle test --info
$ gradle test --tests "javapoets.tokenizer.test.JavaTokenizerTest"
$ gradle test --tests "*JavaTokenizerTest"
$ gradle test --tests "javapoets.tokenizer.test.JavascriptPrettyPrintTokenizerTest"
```

### Force clean rebuild (important)

gradle clean test --rerun-tasks --info

```
$ gradle clean test --rerun-tasks --info
```

## Demo

```
$ ./gradlew run -PmainClass=javapoets.tokenizer.demo.ParserDemo
$ ./gradlew run -PmainClass=javapoets.tokenizer.demo.ObfuscationVistitorDemo
```

---

## Language Support

### Java

* Keywords, operators, punctuation
* Integer / floating point literals
* String and char literals
* Line and block comments

### JavaScript

* ES6+ keywords
* Template literals (basic support)
* Context-aware regex literals
* Modern operators (`===`, `=>`, `??`, etc.)

---

## Design Principles

* **Separation of concerns**
  Lexer, parser, and AST are fully decoupled

* **Extensibility-first**
  New languages can be added via `LanguageDefinition`

* **Deterministic parsing**
  Predictable LL-style parsing using `TokenStream`

* **Performance-aware**
  Operator trie ensures efficient token matching

* **Testability**
  Every layer is independently testable

---

## Use Cases

* Static code analysis tools
* Linters and formatters
* Code search and indexing
* Language tooling / IDE features
* Compiler frontends
* Educational tools for parsing and language design

---

## Roadmap

* [ ] Unary expressions (`!x`, `-x`)
* [ ] Function calls and member access
* [ ] Control flow (`if`, `while`, `for`)
* [ ] AST visitor pattern
* [ ] TypeScript support
* [ ] Full Java parser support
* [ ] Incremental parsing
* [ ] Performance optimizations (zero-copy lexemes)

---

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

---

## License

MIT License

---

## Author

**Dermot Doherty**
Javapoets

---

## Final Thoughts

This project is designed as a **foundation for building language tooling**, not just a tokenizer.

If you're building compilers, analyzers, or developer tools — this is your starting point.
