package com.myGame.simulation.mathbomber.factory;

import com.myGame.simulation.entities.ExplosionCell;
import com.myGame.simulation.entities.MathBomb;
import com.myGame.simulation.entities.MathEnemy;
import com.myGame.simulation.entities.MathPlayer;
import com.myGame.simulation.mathbomber.board.MathBoard;
import com.myGame.simulation.mathbomber.question.ArithmeticQuestionGenerator;

public interface MathBomberFactory {
    MathBoard createBoard(float screenWidth, float screenHeight);
    ArithmeticQuestionGenerator createQuestionGenerator();
    MathPlayer createPlayer(MathBoard board);
    MathEnemy createEnemy(MathBoard board, int row, int col, int value);
    MathBomb createBomb(MathBoard board, int row, int col, float fuseSeconds, int blastRange);
    ExplosionCell createExplosion(MathBoard board, int row, int col, float durationSeconds);
}
