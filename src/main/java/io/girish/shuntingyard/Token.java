package io.girish.shuntingyard;

import io.girish.shuntingyard.enums.TokenType;

public class Token {
    String value;
    int precedence;

    TokenType tokenType;

    public Token(String value, int precedence, TokenType tokenType) {
        this.value = value;
        this.precedence = precedence;
        this.tokenType = tokenType;
    }

    public String getValue() {
        return value;
    }

    public int getPrecedence() {
        return precedence;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
