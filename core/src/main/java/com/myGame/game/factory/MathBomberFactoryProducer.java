package com.myGame.game.factory;

import com.myGame.game.factory.concrete.AdditionMathBomberFactory;
import com.myGame.game.factory.concrete.MixedMathBomberFactory;
import com.myGame.game.factory.concrete.MultiplicationMathBomberFactory;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;

public final class MathBomberFactoryProducer {
    private MathBomberFactoryProducer() {
    }

    public static MathBomberFactory getFactory(MathBomberConfig config, QuestionMode mode) {
        if (mode == null) {
            return new MixedMathBomberFactory(config);
        }
        switch (mode) {
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
