---
title: "Patterns"
summary: "How to create patterns for lexer rules"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

The pattern is the actual syntax that the lexer will use to identify tokens. You can either use raw regex by just providing a string or use the `WordedPattern` utility.

It comes with three methods to define simple patterns:
- `makeOneWordPattern(String word)` - Pattern for one word tokens
- `makeWordedPattern(List<String> keywords)` - Pattern for multiple words for tokens
- `makeSpecialCharacterPattern(char e)` - Pattern for special characters, like `<`, `>` or `!` (or lettersif your boring)
