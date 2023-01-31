package io.girish;

import io.girish.shuntingyard.ShuntingYard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestShuntingYard {
    @Test
    @DisplayName("1 + 1")
    void Test1() throws Exception {
        String expr = "1+1";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(2, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("1 + 1 + 5")
    void Test2() throws Exception {
        String expr = "1+1+5";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(7, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("2 - 4 / 2 + 8 * 2")
    void Test3() throws Exception {
        String expr = "2-4/2+8*2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(16, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("8 / 2 + 2 - 6 / 2 + 3 * 9 - 1")
    void Test4() throws Exception {
        String expr = "8/2+2-6/2+3*9-1";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(29, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("2 - 4 / 2 + 8 * 20")
    void Test5() throws Exception {
        String expr = "2-4/2+8*20";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(160, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("200 * 2000")
    void Test6() throws Exception {
        String expr = "200*2000";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(400000, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("5.5 * 5.5")
    void Test7() throws Exception {
        String expr = "5.5+5.5";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(11, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("6 / 5")
    void Test8() throws Exception {
        String expr = "6/5";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(1.2, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("5.1234 + 5.1234")
    void Test9() throws Exception {
        String expr = "5.1234+5.1234";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(10.2468, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("5.1234 + 5.1234 / 3")
    void Test10() throws Exception {
        String expr = "5.1234+5.1234/3";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(6.8312, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("(5 + 3) / 2")
    void Test11() throws Exception {
        String expr = "(5+3)/2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(4, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("2 * (5.5 + 3) / 2")
    void Test12() throws Exception {
        String expr = "2*(5.5+3)/2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(8.5, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("2 * (5.5 + 3) * 7 / 2")
    void Test13() throws Exception {
        String expr = "2*(5.5+3)*7/2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(59.5, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("2 * sin(sin(30) + 1)")
    void Test14() throws Exception {
        String expr = "2*sin(sin(30)+1)";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(0.05235389662, ShuntingYard.evaluate(expr, expressionVariables), 0.00000001);
    }

    @Test
    @DisplayName("2 * sin(30)")
    void Test15() throws Exception {
        String expr = "2*sin(30)";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(1, ShuntingYard.evaluate(expr, expressionVariables), 0.000000000000001);
    }

    @Test
    @DisplayName("2 * cos(90)")
    void Test16() throws Exception {
        String expr = "2*cos(90)";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(0, ShuntingYard.evaluate(expr, expressionVariables), 0.000000000000001);
    }

    @Test
    @DisplayName("2 * tan(30)")
    void Test17() throws Exception {
        String expr = "2*tan(30)";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(1.154700538, ShuntingYard.evaluate(expr, expressionVariables), 0.00000001);
    }

    @Test
    @DisplayName("2^2")
    void Test18() throws Exception {
        String expr = "2^2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(4, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("(5 + 2) ^ 2")
    void Test19() throws Exception {
        String expr = "(5+2)^2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(49, ShuntingYard.evaluate(expr, expressionVariables));
    }

    @Test
    @DisplayName("sin(30) + 1")
    void Test20() throws Exception {
        String expr = "sin(30)+1";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(1.5, ShuntingYard.evaluate(expr, expressionVariables), 0.000000000000001);
    }

    @Test
    @DisplayName("3 + 4 * 2 / (1 - 5) ^ 2 ^ 3")
    void Test21() throws Exception {
        String expr = "3+4*2/(1-5)^2^3";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(3.00012207, ShuntingYard.evaluate(expr, expressionVariables), 0.000000001);
    }

    @Test
    @DisplayName("-sin(30)")
    void Test22() throws Exception {
        String expr = "-sin(30)";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(-0.5, ShuntingYard.evaluate(expr, expressionVariables), 0.000000001);
    }

    @Test
    @DisplayName("-(-sin(30))")
    void Test23() throws Exception {
        String expr = "-(-sin(30))";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(0.5, ShuntingYard.evaluate(expr, expressionVariables), 0.000000001);
    }

    @Test
    @DisplayName("--(-+--sin(30))")
    void Test24() throws Exception {
        String expr = "--(-+--sin(30))";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(-0.5, ShuntingYard.evaluate(expr, expressionVariables), 0.000000001);
    }

    @Test
    @DisplayName("-1*-2")
    void Test25() throws Exception {
        String expr = "-1*-2";

        HashMap<String, Double> expressionVariables = new HashMap<>();

        assertEquals(2, ShuntingYard.evaluate(expr, expressionVariables));
    }
}
