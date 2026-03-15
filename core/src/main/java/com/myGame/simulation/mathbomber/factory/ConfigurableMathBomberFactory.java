package com.myGame.simulation.mathbomber.factory;

import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.QuestionMode;
import com.myGame.simulation.mathbomber.question.ArithmeticQuestionGenerator;

public class ConfigurableMathBomberFactory extends BaseMathBomberFactory {
    private final QuestionMode mode;

    public ConfigurableMathBomberFactory(MathBomberConfig config, QuestionMode mode) {
        super(config);
        this.mode = mode == null ? QuestionMode.BOTH : mode;
    }

    @Override
    public ArithmeticQuestionGenerator createQuestionGenerator() {
        MathBomberConfig cfg = config();
        switch (mode) {
            case ADDITION_ONLY:
                return new ArithmeticQuestionGenerator(
                        cfg.minAddend,
                        cfg.maxAddend,
                        QuestionMode.ADDITION_ONLY);
            case MULTIPLICATION_ONLY:
                return new ArithmeticQuestionGenerator(
                        cfg.multiplicationMinOperand,
                        cfg.multiplicationMaxOperand,
                        QuestionMode.MULTIPLICATION_ONLY);
            case BOTH:
            default:
                return new ArithmeticQuestionGenerator(
                        cfg.minAddend,
                        cfg.maxAddend,
                        QuestionMode.BOTH);
        }
    }
}
