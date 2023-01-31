package io.girish.cli;

import io.girish.cli.enums.MenuEvent;
import io.girish.core.ExpressionValidator;
import io.girish.core.Graph;
import io.girish.core.PPMColor;
import io.girish.shuntingyard.ShuntingYard;
import io.girish.core.ExpressionParser;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private final HashMap<String, Double> expressionVariables = new HashMap<>();

    public void showWelcome() {
        System.out.println("""
                (c) !matlab CLI 2023.1.0
                
                Type "?" for help.
                """);
    }

    public void showHelp() {
        System.out.println("""

                Type in a mathematical expression like:
                    5*2*sin(30)-1+(2+1)/5
                    
                Declare variables:
                    a=1
                    
                Use variables in expressions:
                    2*a
                    
                Type "plot" to plot functions.
                Type "exit" to exit the program.
                """);
    }

    public void getInput() {
        Scanner scanner = new Scanner(System.in);
        String userInput = "";

        DecimalFormat df = new DecimalFormat("#.########");
        df.setRoundingMode(RoundingMode.CEILING);

        boolean isRunning = true;

        while (isRunning) {
            try {
                System.out.print("!matlab> ");
                userInput = scanner.nextLine();

                // Remove any consecutively entered whitespace from user input
                userInput = userInput.trim().replaceAll("\\s+", " ");

                if (userInput.equals("")) {
                    continue;
                }

                MenuEvent mr = determineEvent(userInput);

                switch (mr) {
                    case SHOW_VARIABLES -> {
                        System.out.println("""
                                                            
                                \tVAR\tVALUE
                                \t---\t-----""");
                        for (Map.Entry<String, Double> exprVar : this.expressionVariables.entrySet()) {
                            System.out.println("\t" + exprVar.getKey() + "\t" + exprVar.getValue());
                        }
                        System.out.println();
                    }
                    case SHOW_HELP -> showHelp();
                    case PLOT -> plotGraph();
                    case EVALUATE_EXPRESSION -> {
                        double expressionResult = ShuntingYard.evaluate(userInput, this.expressionVariables);
                        String formattedExpressionResult = df.format(expressionResult);
                        System.out.println(userInput + "=" + formattedExpressionResult);
                    }
                    case DECLARE_VARIABLE -> {
                        List<String> variableDeclaration = ExpressionParser.parseVariableDeclaration(userInput);

                        String variableName = variableDeclaration.get(0);
                        String variableValue = variableDeclaration.get(1);

                        double evaluatedVariable = storeVariable(variableName, variableValue);

                        System.out.println("Stored variable " + variableName + "=" + evaluatedVariable);
                    }
                    case EXIT -> isRunning = false;
                }
            } catch (Exception e) {
                System.out.println("Error in expression: " + "\"" + userInput + "\".");
            }
        }
    }

    /*
     * Settings for demo graphs:

     *      Sine wave:
     *      ----------
     *      a=40, f=10
     *      f(x)=a*sin(f*x)
     *      step(x)=0.1
     *      min(x)=0
     *      max(x)=499
     *
     *      Parabola:
     *      ---------
     *      f(x)=x^2
     *      step(x)=0.1
     *      min(x)=-10
     *      max(x)=11
     */
    private void plotGraph() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("""
                
                PLOT FUNCTION
                -------------
                """);

        System.out.print("f(x)=");
        String expression = scanner.nextLine();

        System.out.print("step(x)=");
        double step = Double.parseDouble(scanner.nextLine());

        System.out.print("min(x)=");
        double min = Double.parseDouble(scanner.nextLine());

        System.out.print("max(x)=");
        double max = Double.parseDouble(scanner.nextLine());

        System.out.print("\nGraph location: ");
        String imageLocation = scanner.nextLine();

        Graph graph = new Graph(500, 500);
        graph.setLineColor(PPMColor.RED);
        graph.setBackgroundColor(PPMColor.BLACK);

        for (double x = min; x < max; x += step) {
            this.expressionVariables.put("x", x);

            double y = ShuntingYard.evaluate(expression, this.expressionVariables);

            // We round the calculated points to whole numbers so
            // that we don't try to plot fractional pixels.
            graph.addPoint((int) Math.round(x), (int) Math.round(y));
        }

        graph.writeImage(imageLocation);

        System.out.println("\nSaved plot to \"" + imageLocation + "\".\n");
    }

    public double storeVariable(String variableName, String variableValue) throws Exception {
        double evaluatedVariableValue = ShuntingYard.evaluate(variableValue, this.expressionVariables);
        this.expressionVariables.put(variableName, evaluatedVariableValue);
        return evaluatedVariableValue;
    }

    public MenuEvent determineEvent(String expr) {
        if (ExpressionValidator.isVariableDeclaration(expr)) {
            return MenuEvent.DECLARE_VARIABLE;
        }  else if (expr.equals("show variables;")) {
            return MenuEvent.SHOW_VARIABLES;
        } else if (expr.equals("?")) {
            return MenuEvent.SHOW_HELP;
        } else if (expr.equals("plot")) {
            return MenuEvent.PLOT;
        } else if (expr.equals("exit")) {
            return MenuEvent.EXIT;
        } else {
            return MenuEvent.EVALUATE_EXPRESSION;
        }
    }
}
