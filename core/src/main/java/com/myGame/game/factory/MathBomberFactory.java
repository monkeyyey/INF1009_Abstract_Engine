package com.myGame.game.factory;

import com.myGame.engine.animation.AnimationComponent;
import com.myGame.game.animation.MathBombAnimationState;
import com.myGame.game.animation.MathPlayerAnimationState;
import com.myGame.game.entities.ExplosionCell;
import com.myGame.game.entities.MathBomb;
import com.myGame.game.entities.MathEnemy;
import com.myGame.game.entities.MathPlayer;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.questions.ArithmeticQuestionGenerator;

public interface MathBomberFactory {
    Board createBoard(float screenWidth, float screenHeight);
    ArithmeticQuestionGenerator createQuestionGenerator();
    MathPlayer createPlayer(Board board, AnimationComponent<MathPlayerAnimationState> animation);
    MathEnemy createEnemy(Board board, int row, int col, int value);
    MathBomb createBomb(Board board,
                        int row,
                        int col,
                        float fuseSeconds,
                        int blastRange,
                        AnimationComponent<MathBombAnimationState> animation);
    ExplosionCell createExplosion(Board board, int row, int col, float durationSeconds);
}
