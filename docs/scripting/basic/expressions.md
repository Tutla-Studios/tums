---
title: "Expressions"
summary: "How to use expressions in tums"
created: "2026-05-23"
updated: "2026-05-23"
isdoc: true
---

Expressions are a bit complicated but once you understand them it will all be a piece of cake. There are 4 types of Expressions (its not exactly types, they form a tree going from root to shoot below) below:

- [factor]
- [term]
- [expression]
- [condition]

## Factor

This is the atom of TuSK. A Factor takes in a `NUMBER`, `STRING`, `BOOL`, `TYPE`, `KEYWORD` or `IDENTIFIER`. A Factor could be:

- A Number
- A String
- A Boolean (true/false)
- A Type (any of the above)
- `( [expression] )` (So we can use brackets and combine [expression]s, this lets put [expression]s inside [expression]s aswell)
- A Function (aka `IDENTIFIER`)
- A Variable (aka `IDENTIFIER`)
  - Could be just a variable
  - Could be a property
  - Could be an item of a list/string, to access an item of a list/string you can `[ordinal number] item in [expression]` where ordinal number is 1st 2nd 3rd 4th 5th... and expression is a list/string
    - Example `3rd item in "javascript", "python", "kungfu"` returns `"kungfu"`
- A List 
  - Any expression seperated by commas will be made into a list
  - For example: `"bats", "balls", "wickets"` makes a list
- A function (effects/functions that return variables, you could make your own or read the [effects list](/tums/scripting/basic/effects)
  
## Term

A Term could be a [factor] or a `[factor] (/|*|^|%|) [factor]`:

- [factor] (Look at the previous heading, everything under there comes here even expressions because of parenthesis)
- [factor] * [factor]: Multiplication
- [factor] / [factor]: Division
- [factor] ^ [factor] or [factor] % [factor]
  
## Expression

Expressions are used everywhere in the language, they could be a:

- [term]
- [term] + [expression]
- [term] - [expression]
- [term] COMAPARISON [expression]
  - COMPARISION could be is, >, < =, ==, !=
  - returns a boolean (true/false)

## Condition

Conditions return true or false depending on what you give them

- `not` [condition]: returns the opposite of the condition, for example if it was true it will return false
- [expression] where it is a BOOL (true/false)
- [expression] LOGIC [expression]
  - LOGIC can be and, or, contains, in
  - [expression] `and` [expression]: returns true if both are true
  - [expression] `or` [expression]: returns true if either are true
  - [expression] contains [expression]: returns true if expression2 is in expression1
  - [expression] in [expression]: returns true if expression1 is in expression2
  