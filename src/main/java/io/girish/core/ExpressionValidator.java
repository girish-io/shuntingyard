package io.girish.core;

import java.util.List;

public class ExpressionValidator {
    public static boolean isVariableDeclaration(String expr) {
        List<String> splitExpression = ExpressionParser.parseVariableDeclaration(expr);
        return splitExpression.size() == 2;
    }
}
