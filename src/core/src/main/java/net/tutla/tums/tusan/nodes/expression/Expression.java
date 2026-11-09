package net.tutla.tums.tusan.nodes.expression;

import net.tutla.tums.tusan.TusanContext;
import net.tutla.tums.tusan.node.Node;


/// Expressions are a bit complicated but once you understand them it will all be a piece of cake. There are 4 types of Expressions (its not exactly types, they form a tree going from root to shoot below) below:
///
/// - [factor]
/// - [term]
/// - [expression]
/// - [condition]
///
/// <!-- include Factor -->
///
/// <!-- include Term -->
///
/// ## Expression
///
/// Expressions are used everywhere in the language, they could be a:
///
/// - [term]
/// - [term] + [term]
/// - [term] - [term]
/// - [expression] COMPARISON [expression]
///   - COMPARISON could be is, >, <, >=, <=, ==, !=
///   - returns a boolean (true/false)
///
/// They are worked out left to right, so `10 - 2 - 3` is `5`, and the maths comes before the comparison, so `n + 1 == 3` compares `n + 1` with `3`.
///
/// ## Condition
///
/// Conditions return true or false depending on what you give them
///
/// - `not` [condition]: returns the opposite of the condition, for example if it was true it will return false
/// - [expression] where it is a BOOL (true/false)
/// - [expression] LOGIC [expression]
///   - LOGIC can be and, or, contains, in
///   - [expression] `and` [expression]: returns true if both are true
///   - [expression] `or` [expression]: returns true if either are true
///   - [expression] contains [expression]: returns true if expression2 is in expression1
///   - [expression] in [expression]: returns true if expression1 is in expression2
///
/// `not` comes before `and`, and `and` before `or`, so `not a or b and c` means `(not a) or (b and c)`. Use brackets to group them any other way.
///
/// @tums.title Expressions
/// @tums.summary How to use expressions in tums
public class Expression extends Node {
    public Object value;

    public Expression(TusanContext ctx){
        super(ctx);
    }

    public Expression create(){
        // the loosest operator is or, so an expression is an Or, which reads everything above it:
        // Or > And > Not > Comparison > Sum > Term > Power > Factor
        value = new Or(ctx).create().value;
        return this;
    }
}
