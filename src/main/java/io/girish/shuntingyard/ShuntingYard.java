package io.girish.shuntingyard;

import io.girish.shuntingyard.enums.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/*
 * Implementation of the shunting yard algorithm for solving mathematical expressions.
 * It converts an infix expression to postfix/Reverse Polish Notation (RPN) which can then be evaluated by a computer,
 * taking into account the order of precedence of different operators such as parentheses, multiplication, division,
 * subtraction (PEMDAS) etc.
 *
 * This implementation of the algorithm has been extended to support numeric values greater than 9, negative values,
 * functions, variable declaration and evaluation, and decimal values.
 *
 * Reference: https://en.wikipedia.org/wiki/Shunting_yard_algorithm
 */
public class ShuntingYard {
    public static double evaluate(String expression, HashMap<String, Double> expressionVariables) throws Exception {
        Tokenizer tokenizer = new Tokenizer(expression);

        List<Token> outputQueue = new ArrayList<>();
        List<Token> operatorStack = new ArrayList<>();

        List<Token> tokens = tokenizer.getTokens();

        for (int tokenIdx = 0; tokenIdx < tokens.size(); tokenIdx++) {
            Token token = tokens.get(tokenIdx);

            switch (token.getTokenType()) {
                case NUMBER -> outputQueue.add(token);
                case VARIABLE -> {
                    double variableValue = expressionVariables.get(token.getValue());
                    outputQueue.add(new Token(String.valueOf(variableValue), token.getPrecedence(), TokenType.NUMBER));
                }
                case FUNCTION -> {
                    int functionTokenIdx = tokenIdx + 2;
                    Token functionToken = tokens.get(functionTokenIdx);

                    StringBuilder stringBuilder = new StringBuilder();

                    int countOpeningParentheses = 1;

                    while (true) {
                        if (functionToken.getTokenType() == TokenType.OPENING_PARENTHESES) {
                            countOpeningParentheses += 1;
                        }

                        if (functionToken.getTokenType() == TokenType.CLOSING_PARENTHESES) {
                            countOpeningParentheses -= 1;

                            if (countOpeningParentheses <= 0) {
                                stringBuilder.append(functionToken.getValue());
                                break;
                            }
                        }

                        stringBuilder.append(functionToken.getValue());

                        functionTokenIdx += 1;

                        if (functionTokenIdx == tokens.size()) {
                            break;
                        }

                        functionToken = tokens.get(functionTokenIdx);
                    }

                    double functionExprResult = evaluate(stringBuilder.toString(), expressionVariables);

                    Token functionResult = null;

                    try {
                        functionResult = executeFunction(token, functionExprResult);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }

                    outputQueue.add(functionResult);

                    tokenIdx = functionTokenIdx;
                }
                case OPENING_PARENTHESES -> operatorStack.add(token);
                case CLOSING_PARENTHESES -> {
                    if (operatorStack.size() != 0) {
                        int lastTokenIdx = operatorStack.size() - 1;
                        Token lastToken = operatorStack.get(lastTokenIdx);

                        while (lastToken.getTokenType() != TokenType.OPENING_PARENTHESES && operatorStack.size() != 0) {
                            outputQueue.add(operatorStack.remove(lastTokenIdx));

                            lastTokenIdx = operatorStack.size() - 1;

                            if (!(lastTokenIdx < 0)) {
                                lastToken = operatorStack.get(lastTokenIdx);
                            }
                        }
                        if (!(lastTokenIdx < 0)) {
                            operatorStack.remove(operatorStack.size() - 1);
                        }
                    }
                }
                case OPERATOR -> {
                    if (operatorStack.size() != 0) {
                        int lastOperatorIdx = operatorStack.size() - 1;
                        Token lastOperator = operatorStack.get(lastOperatorIdx);

                        while (lastOperator.getPrecedence() >= token.getPrecedence() && operatorStack.size() != 0) {
                            if (lastOperator.getValue().equals("^") && token.getValue().equals("^")) {
                                break;
                            }

                            Token op = operatorStack.remove(lastOperatorIdx);
                            outputQueue.add(op);

                            lastOperatorIdx = operatorStack.size() - 1;

                            if (!(lastOperatorIdx < 0)) {
                                lastOperator = operatorStack.get(lastOperatorIdx);
                            }
                        }
                    }

                    operatorStack.add(token);
                }
            }
        }

        Collections.reverse(operatorStack);

        outputQueue.addAll(operatorStack);

        List<Token> stack = new ArrayList<>();

        for (Token token : outputQueue) {
            switch (token.getTokenType()) {
                case NUMBER -> stack.add(token);
                case OPERATOR -> {
                    Token t1 = stack.remove(stack.size() - 1);
                    Token t2 = stack.remove(stack.size() - 1);

                    try {
                        Token calculated = calculateStack(t2, t1, token);
                        stack.add(calculated);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }
                }
            }
        }

        double result = Double.parseDouble(stack.get(0).getValue());

        return result == -0 ? 0 : result;
    }

    static private Token calculateStack(Token t1, Token t2, Token operator) throws Exception {
        double t1Number = Double.parseDouble(t1.getValue());
        double t2Number = Double.parseDouble(t2.getValue());

        double calculated = switch (operator.getValue()) {
            case "*" -> t1Number * t2Number;
            case "+" -> t1Number + t2Number;
            case "-" -> t1Number - t2Number;
            case "/" -> t1Number / t2Number;
            case "^" -> Math.pow(t1Number, t2Number);

            default -> throw new Exception("Invalid expression at: " + t1 + operator + t2);
        };

        return new Token(String.valueOf(calculated), 0, TokenType.NUMBER);
    }

    static private Token executeFunction(Token function, double functionParam) throws Exception {
        double functionResult = switch (function.getValue()) {
            case "SIN" -> Math.sin(Math.toRadians(functionParam));
            case "COS" -> Math.cos(Math.toRadians(functionParam));
            case "TAN" -> Math.tan(Math.toRadians(functionParam));
            default -> throw new Exception("No function implemented with name : \"" + function.getValue() + "\"");
        };

        String functionResultStr = String.valueOf(functionResult);
        int functionResultPrecedence = -1;

        try {
            functionResultPrecedence = Tokenizer.getPrecedence(functionResultStr, TokenType.NUMBER);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return new Token(String.valueOf(functionResult), functionResultPrecedence, TokenType.NUMBER);
    }
}
