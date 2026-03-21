package com.myGame.game.factory.concrete;

import com.myGame.game.entities.MathEnemy;
import com.myGame.game.entities.SquareMathEnemy;
import com.myGame.game.factory.base.BaseMathBomberFactory;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;
import com.myGame.game.mathbomber.questions.ArithmeticQuestionGenerator;

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
                QuestionMode.BOTH);
    }

    @Override
    public MathEnemy createEnemy(Board board, int row, int col, int value) {
        return new SquareMathEnemy(
                row,
                col,
                value,
                board.getTileSize(),
                board.getBoardX(),
                board.getBoardY());
    }
}
