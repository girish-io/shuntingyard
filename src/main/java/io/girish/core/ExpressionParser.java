package io.girish.core;

import java.util.List;

public class ExpressionParser {
    public static List<String> parseVariableDeclaration(String expr) {
        // Remove any whitespace from expression
        expr = expr.replaceAll("\\s", "");

        return List.of(expr.split("="));
    }
}
