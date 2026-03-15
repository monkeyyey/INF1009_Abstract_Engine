package com.myGame.simulation.mathbomber.question;

import com.badlogic.gdx.math.MathUtils;
import com.myGame.simulation.mathbomber.config.QuestionMode;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticQuestionGenerator {
    public enum Operation {
        ADDITION("+"),
        MULTIPLICATION("x");

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    private final int minAddend;
    private final int maxAddend;
    private final QuestionMode questionMode;

    public ArithmeticQuestionGenerator(int minAddend, int maxAddend) {
        this(minAddend, maxAddend, QuestionMode.BOTH);
    }

    public ArithmeticQuestionGenerator(int minAddend,
                                     int maxAddend,
                                     QuestionMode questionMode) {
        this.minAddend = minAddend;
        this.maxAddend = maxAddend;
        this.questionMode = questionMode == null ? QuestionMode.BOTH : questionMode;
    }

    public ArithmeticQuestion nextQuestion() {
        int a = MathUtils.random(minAddend, maxAddend);
        int b = MathUtils.random(minAddend, maxAddend);
        Operation op = pickOperation();
        return new ArithmeticQuestion(a, b, op);
    }

    private Operation pickOperation() {
        switch (questionMode) {
            case ADDITION_ONLY:
                return Operation.ADDITION;
            case MULTIPLICATION_ONLY:
                return Operation.MULTIPLICATION;
            case BOTH:
            default:
                return MathUtils.randomBoolean() ? Operation.ADDITION : Operation.MULTIPLICATION;
        }
    }

    public List<Integer> buildEnemyValues(int answer, int enemyCount, Operation operation) {
        List<Integer> values = new ArrayList<>();
        values.add(answer);
        Operation resolvedOperation = operation == null ? pickOperation() : operation;
        int minAnswer = derivedMinAnswer(resolvedOperation);
        int maxAnswer = derivedMaxAnswer(resolvedOperation);
        while (values.size() < enemyCount) {
            int value = MathUtils.random(minAnswer, maxAnswer);
            if (value == answer) continue;
            values.add(value);
        }
        return values;
    }

    private int derivedMinAnswer(Operation operation) {
        switch (operation) {
            case ADDITION:
                return minAddend + minAddend;
            case MULTIPLICATION:
                return minAddend * minAddend;
            default:
                return minAddend + minAddend;
        }
    }

    private int derivedMaxAnswer(Operation operation) {
        switch (operation) {
            case ADDITION:
                return maxAddend + maxAddend;
            case MULTIPLICATION:
                return maxAddend * maxAddend;
            default:
                return maxAddend + maxAddend;
        }
    }

    public static final class ArithmeticQuestion {
        private final int left;
        private final int right;
        private final Operation operation;
        private final int answer;

        public ArithmeticQuestion(int left, int right, Operation operation) {
            this.left = left;
            this.right = right;
            this.operation = operation;
            this.answer = operation == Operation.ADDITION ? left + right : left * right;
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

        public Operation getOperation() {
            return operation;
        }

        public String getOperatorSymbol() {
            return operation.getSymbol();
        }

        public int getAnswer() {
            return answer;
        }
    }
}
