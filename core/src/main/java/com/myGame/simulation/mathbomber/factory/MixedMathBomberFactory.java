package com.myGame.simulation.mathbomber.factory;

import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.QuestionMode;
import com.myGame.simulation.mathbomber.question.ArithmeticQuestionGenerator;

public class MixedMathBomberFactory extends BaseMathBomberFactory {
    public MixedMathBomberFactory(MathBomberConfig config) {
        super(config);
    }

    @Override
    public ArithmeticQuestionGenerator createQuestionGenerator() {
        MathBomberConfig cfg = config();
        return new ArithmeticQuestionGenerator(
                cfg.minAddend,
                cfg.maxAddend,
                cfg.minAnswer,
                cfg.maxAnswer,
                QuestionMode.BOTH);
    }
}
