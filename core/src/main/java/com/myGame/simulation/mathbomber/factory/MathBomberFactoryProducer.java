package com.myGame.simulation.mathbomber.factory;

import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.QuestionMode;

public final class MathBomberFactoryProducer {
    private MathBomberFactoryProducer() {
    }

    public static MathBomberFactory getFactory(MathBomberConfig config, QuestionMode mode) {
        return new ConfigurableMathBomberFactory(config, mode);
    }
}
