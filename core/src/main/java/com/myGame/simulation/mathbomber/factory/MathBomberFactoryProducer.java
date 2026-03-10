package com.myGame.simulation.mathbomber.factory;

import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.QuestionMode;

public final class MathBomberFactoryProducer {
    private MathBomberFactoryProducer() {
    }

    public static MathBomberFactory getFactory(MathBomberConfig config, QuestionMode mode) {
        QuestionMode resolvedMode = mode == null ? QuestionMode.BOTH : mode;
        switch (resolvedMode) {
            case ADDITION_ONLY:
                return new AdditionMathBomberFactory(config);
            case MULTIPLICATION_ONLY:
                return new MultiplicationMathBomberFactory(config);
            case BOTH:
            default:
                return new MixedMathBomberFactory(config);
        }
    }
}
