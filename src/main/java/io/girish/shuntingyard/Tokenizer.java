package io.girish.shuntingyard;

import io.girish.shuntingyard.enums.Function;
import io.girish.shuntingyard.enums.TokenPrecedence;
import io.girish.shuntingyard.enums.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private List<Token> tokens = new ArrayList<>();

    public Tokenizer(String expression) throws Exception {
        // Remove any whitespace from expression
        expression = expression.replaceAll("\\s", "");

        List<String> expressionTokens = List.of(expression.split(""));

        List<String> characterCombination = new ArrayList<>();

        // Parse and determine the type of the tokens in the expression
        for (int exprTokenIdx = 0; exprTokenIdx < expressionTokens.size(); exprTokenIdx++) {
            String exprToken = expressionTokens.get(exprTokenIdx);
            TokenType tokenType;

            if (isNumeric(exprToken)) {
                tokenType = TokenType.NUMBER;
            } else if (isOperator(exprToken)) {
                tokenType = TokenType.OPERATOR;
            } else if (isOpeningParentheses(exprToken)) {
                tokenType = TokenType.OPENING_PARENTHESES;
            } else if (isClosingParentheses(exprToken)) {
                tokenType = TokenType.CLOSING_PARENTHESES;
            } else if (isCharacter(exprToken)) {
                characterCombination.add(exprToken);

                if ((exprTokenIdx + 1) != expressionTokens.size()) {
                    continue;
                } else {
                    tokenType = TokenType.VARIABLE;
                }
            } else {
                throw new Exception("Invalid expression : " + exprToken);
            }

            // If there is a character combination in the expression, check whether it
            // matches an internal function otherwise treat it as a variable.
            if ((!isCharacter(exprToken) && characterCombination.size() != 0) ||
                    (isCharacter(exprToken) && ((exprTokenIdx + 1) == expressionTokens.size()))) {
                String characterCombo = String.join("", characterCombination);

                Function determinedFunction = determineFunction(characterCombo);

                if (determinedFunction == null) {
                    for (int variableIdx = 0; variableIdx < characterCombination.size(); variableIdx++) {
                        String variable = characterCombination.get(variableIdx);

                        int variableTokenPrecedence = -1;

                        try {
                            variableTokenPrecedence = getPrecedence(variable, TokenType.VARIABLE);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            System.exit(1);
                        }

                        Token variableToken = new Token(variable, variableTokenPrecedence, TokenType.VARIABLE);

                        tokens.add(variableToken);

                        // Expressions like ax*2 become a * x * 2
                        if (variableIdx + 1 != characterCombination.size()) {
                            tokens.add(new Token("*", TokenPrecedence.MULTIPLY.getPrecedence(), TokenType.OPERATOR));
                        }
                    }
                } else {
                    switch (determinedFunction) {
                        case SIN -> tokens.add(new Token("SIN", TokenPrecedence.FUNCTION.getPrecedence(), TokenType.FUNCTION));
                        case COS -> tokens.add(new Token("COS", TokenPrecedence.FUNCTION.getPrecedence(), TokenType.FUNCTION));
                        case TAN -> tokens.add(new Token("TAN", TokenPrecedence.FUNCTION.getPrecedence(), TokenType.FUNCTION));
                    }
                }

                characterCombination.clear();
            }

            int tokenPrecedence = -1;

            try {
                tokenPrecedence = getPrecedence(exprToken, tokenType);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }

            Token token = new Token(exprToken, tokenPrecedence, tokenType);

            tokens.add(token);
        }

        while (foundConsecutiveOperators()) {
            tokenizeOperators();
        }

        tokenizeNumbers();
        tokenizeParentheses();
        tokenizeNumbersWithOperators();
    }

    private boolean foundConsecutiveOperators() {
        for (int i = 0; i < this.tokens.size(); i++) {
            Token token = this.tokens.get(i);

            if (i + 1 != this.tokens.size()) {
                Token nextToken = this.tokens.get(i + 1);
                String combinedOp = token.getValue() + nextToken.getValue();

                if (combinedOp.equals("+-") || combinedOp.equals("-+") || combinedOp.equals("--") || combinedOp.equals("++")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void tokenizeParentheses() {
        List<Token> newTokens = new ArrayList<>();

        for (int i = 0; i < this.tokens.size(); i++) {
            Token token = this.tokens.get(i);

            if (i + 1 != this.tokens.size()) {
                Token nextToken = this.tokens.get(i + 1);

                if ((token.getTokenType() == TokenType.OPERATOR && nextToken.getTokenType() == TokenType.OPENING_PARENTHESES && (token.getValue().equals("+") || token.getValue().equals("-"))) ||
                        token.getTokenType() == TokenType.OPERATOR && nextToken.getTokenType() == TokenType.FUNCTION && (token.getValue().equals("+") || token.getValue().equals("-"))) {
                    newTokens.add(token);
                    newTokens.add(new Token("1", TokenPrecedence.NUMBER.getPrecedence(), TokenType.NUMBER));
                    newTokens.add(new Token("*", TokenPrecedence.MULTIPLY.getPrecedence(), TokenType.OPERATOR));
                    continue;
                }
            }
            newTokens.add(token);
        }

        this.tokens = newTokens;
    }

    private void tokenizeOperators() {
        List<Token> newTokens = new ArrayList<>();

        for (int i = 0; i < this.tokens.size(); i++) {

            Token token = this.tokens.get(i);

            if (i + 1 != this.tokens.size()) {
                Token nextToken = this.tokens.get(i + 1);

                String combinedOp = token.getValue() + nextToken.getValue();

                String newOp = null;

                if (combinedOp.equals("+-") || combinedOp.equals("-+")) {
                    newOp = "-";
                } else if (combinedOp.equals("--") || combinedOp.equals("++")) {
                    newOp = "+";
                }

                if (newOp != null) {
                    int precedence = -1;

                    try {
                        precedence = getPrecedence(newOp, TokenType.OPERATOR);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }

                    token = new Token(newOp, precedence, TokenType.OPERATOR);

                    i += 1;
                }
            }
            newTokens.add(token);
        }

        this.tokens = newTokens;
    }

    private void tokenizeNumbersWithOperators() {
        List<Token> newTokens = new ArrayList<>();

        for (int i = 0; i < getTokens().size(); i++) {
            Token token = getTokens().get(i);

            if (getTokens().size() > i + 1) {
                Token nextToken = getTokens().get(i + 1);

                if ((token.getValue().equals("*") || token.getValue().equals("/")) && (nextToken.getValue().equals("-") || nextToken.getValue().equals("+"))) {
                    newTokens.add(token);
                    newTokens.add(new Token(nextToken.getValue() + "1", TokenPrecedence.NUMBER.getPrecedence(), TokenType.NUMBER));
                    newTokens.add(new Token("*", TokenPrecedence.MULTIPLY.getPrecedence(), TokenType.OPERATOR));

                    i += 1;
                    continue;
                }

                if (i == 0 && token.getTokenType() == TokenType.OPERATOR && nextToken.getTokenType() == TokenType.NUMBER ||
                        i == 0 && token.getTokenType() == TokenType.OPERATOR && nextToken.getTokenType() == TokenType.FUNCTION ||
                    token.getTokenType() == TokenType.OPENING_PARENTHESES && nextToken.getTokenType() == TokenType.OPERATOR) {

                    int precedence = -1;
                    String combinedNumber;

                    if (token.getTokenType() == TokenType.OPENING_PARENTHESES && nextToken.getTokenType() == TokenType.OPERATOR && getTokens().size() > i + 2) {
                        Token opToken = getTokens().get(i + 1);
                        Token numberToken = getTokens().get(i + 2);

                        if (numberToken.getTokenType() == TokenType.FUNCTION) {
                            newTokens.add(new Token(opToken.getValue() + "1", TokenPrecedence.NUMBER.getPrecedence(), TokenType.NUMBER));
                            newTokens.add(new Token("*", TokenPrecedence.MULTIPLY.getPrecedence(), TokenType.OPERATOR));
                            newTokens.add(numberToken);

                            i += 2;

                            continue;
                        }

                        combinedNumber = opToken.getValue() + numberToken.getValue();
                        newTokens.add(token);
                        i += 2;
                    } else {
                        combinedNumber = token.getValue() + nextToken.getValue();
                        i += 1;
                    }

                    try {
                        precedence = getPrecedence(combinedNumber, TokenType.NUMBER);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }

                    token = new Token(combinedNumber, precedence, TokenType.NUMBER);
                }
            }

            newTokens.add(token);
        }

        this.tokens = newTokens;
    }

    /*
     * Combines multiple numeric tokens into one token if they were entered consecutively
     * e.g.: Array {"5", ".", "5", "+", "5", ".", "5"} becomes {"5.5", "+", "5.5"}
     */
    private void tokenizeNumbers() {
        List<Token> newTokens = new ArrayList<>();
        List<Token> numberTokens = new ArrayList<>();

        for (int i = 0; i < getTokens().size(); i++) {
            Token token = getTokens().get(i);

            if (token.getTokenType() == TokenType.NUMBER) {
                numberTokens.add(token);
            }

            if (token.getTokenType() == TokenType.OPERATOR ||
                    token.getTokenType() == TokenType.OPENING_PARENTHESES ||
                    token.getTokenType() == TokenType.CLOSING_PARENTHESES ||
                    token.getTokenType() == TokenType.FUNCTION ||
                    token.getTokenType() == TokenType.VARIABLE ||
                    i + 1 == getTokens().size()) {
                StringBuilder stringBuilder = new StringBuilder();

                for (Token nToken : numberTokens) {
                    stringBuilder.append(nToken.getValue());
                }

                int precedence = -1;

                try {
                    precedence = getPrecedence(stringBuilder.toString(), TokenType.NUMBER);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }

                if (!stringBuilder.toString().equals("")) {
                    Token numberToken = new Token(stringBuilder.toString(), precedence, TokenType.NUMBER);

                    newTokens.add(numberToken);
                }

                if (i + 1 != getTokens().size() || token.getTokenType() == TokenType.CLOSING_PARENTHESES) {
                    newTokens.add(token);
                }

                numberTokens.clear();
            }
        }

        this.tokens = newTokens;
    }

    //private

    /*
     * Gets the precedence of operators according to PEMDAS
     */
    public static int getPrecedence(String exprToken, TokenType tokenType) throws Exception {
        if (tokenType == TokenType.NUMBER || tokenType == TokenType.VARIABLE) {
            return 1;
        } else if (tokenType == TokenType.OPENING_PARENTHESES || tokenType == TokenType.CLOSING_PARENTHESES) {
            return 0;
        } else {
            return switch (exprToken) {
                case "+" -> TokenPrecedence.ADD.getPrecedence();
                case "-" -> TokenPrecedence.SUBTRACT.getPrecedence();
                case "/" -> TokenPrecedence.DIVIDE.getPrecedence();
                case "*" -> TokenPrecedence.MULTIPLY.getPrecedence();
                case "^" -> TokenPrecedence.EXPONENTIAL.getPrecedence();
                default -> throw new Exception("Invalid token: " + exprToken);
            };
        }
    }

    private boolean isNumeric(String exprToken) {
        return switch (exprToken) {
            case "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." -> true;
            default -> false;
        };
    }

    private boolean isOperator(String exprToken) {
        return switch (exprToken) {
            case "+", "-", "*", "/", "^" -> true;
            default -> false;
        };
    }

    private boolean isCharacter(String exprToken) {
        char[] alphabet = {
                'A', 'B', 'C', 'D',
                'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X',
                'Y', 'Z'
        };

        for (char c : alphabet) {
            if (exprToken.toUpperCase().equals(String.valueOf(c))) {
                return true;
            }
        }

        return false;
    }

    private boolean isOpeningParentheses(String exprToken) {
        return exprToken.equals("(");
    }

    private boolean isClosingParentheses(String exprToken) {
        return exprToken.equals(")");
    }

    private Function determineFunction(String tokenCombination) {
        return switch (tokenCombination.toUpperCase()) {
            case "SIN" -> Function.SIN;
            case "COS" -> Function.COS;
            case "TAN" -> Function.TAN;
            default -> null;
        };
    }

    public List<Token> getTokens() {
        return this.tokens;
    }
}
