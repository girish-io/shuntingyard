package io.girish.shuntingyard.enums;

public enum TokenPrecedence {
    NUMBER(1),
    ADD(1),
    SUBTRACT(1),
    DIVIDE(2),
    MULTIPLY(3),
    FUNCTION(5),
    EXPONENTIAL(4);

     final private int precedence;

    TokenPrecedence(int precedence) {
        this.precedence = precedence;
    }

    public int getPrecedence() {
        return precedence;
    }
}
