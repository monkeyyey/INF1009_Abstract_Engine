package com.myGame.simulation.mathbomber.config;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class MathBomberConfig {
    public static final class Direction {
        private final int rowDelta;
        private final int colDelta;

        public Direction(int rowDelta, int colDelta) {
            this.rowDelta = rowDelta;
            this.colDelta = colDelta;
        }

        public int getRowDelta() {
            return rowDelta;
        }

        public int getColDelta() {
            return colDelta;
        }
    }

    private static final List<Direction> CARDINAL_DIRECTIONS = List.of(
            new Direction(0, 1),
            new Direction(0, -1),
            new Direction(1, 0),
            new Direction(-1, 0));

    public final int rows;
    public final int cols;
    public final float tileSize;

    public final int winScore;
    public final int enemyCount;
    public final int minAddend;
    public final int maxAddend;
    public final int multiplicationMinOperand;
    public final int multiplicationMaxOperand;
    public final float questionTimeSeconds;
    public final int bombsPerQuestion;
    public final int startingLives;

    public final float playerSpeedUnitsPerSec;
    public final float enemyMoveDurationSeconds;
    public final float bombFuseSeconds;
    public final float explosionDurationSeconds;
    public final int bombBlastRange;
    public final int playerSpawnRow;
    public final int playerSpawnCol;
    public final Color playerColor;
    public final String difficultyName;

    public static List<Direction> cardinalDirections() {
        return CARDINAL_DIRECTIONS;
    }

    public MathBomberConfig(int rows,
                            int cols,
                            float tileSize,
                            int winScore,
                            int enemyCount,
                            int minAddend,
                            int maxAddend,
                            int multiplicationMinOperand,
                            int multiplicationMaxOperand,
                            float questionTimeSeconds,
                            int bombsPerQuestion,
                            int startingLives,
                            float playerSpeedUnitsPerSec,
                            float enemyMoveDurationSeconds,
                            float bombFuseSeconds,
                            float explosionDurationSeconds,
                            int bombBlastRange,
                            int playerSpawnRow,
                            int playerSpawnCol,
                            Color playerColor,
                            String difficultyName) {
        this.rows = rows;
        this.cols = cols;
        this.tileSize = tileSize;
        this.winScore = winScore;
        this.enemyCount = enemyCount;
        this.minAddend = minAddend;
        this.maxAddend = maxAddend;
        this.multiplicationMinOperand = multiplicationMinOperand;
        this.multiplicationMaxOperand = multiplicationMaxOperand;
        this.questionTimeSeconds = questionTimeSeconds;
        this.bombsPerQuestion = bombsPerQuestion;
        this.startingLives = startingLives;
        this.playerSpeedUnitsPerSec = playerSpeedUnitsPerSec;
        this.enemyMoveDurationSeconds = enemyMoveDurationSeconds;
        this.bombFuseSeconds = bombFuseSeconds;
        this.explosionDurationSeconds = explosionDurationSeconds;
        this.bombBlastRange = bombBlastRange;
        this.playerSpawnRow = playerSpawnRow;
        this.playerSpawnCol = playerSpawnCol;
        this.playerColor = playerColor;
        this.difficultyName = difficultyName;
    }

    public static MathBomberConfig easyConfig() {
        float tile = 48f;
        return new MathBomberConfig(
                9, 11, tile,
                3, 3,
                1, 5,
                1, 5,
                60f, 5, 3,
                tile / 0.36f,
                0.60f,
                1.8f,
                0.20f,
                2,
                1, 2,
                Color.CYAN,
                "Easy"
        );
    }

    public static MathBomberConfig mediumConfig() {
        float tile = 48f;
        return new MathBomberConfig(
                11, 13, tile,
                4, 4,
                6, 10,
                6, 10,
                45f, 4, 3,
                tile / 0.36f,
                0.48f,
                1.8f,
                0.20f,
                2,
                1, 2,
                Color.CYAN,
                "Medium"
        );
    }

    public static MathBomberConfig hardConfig() {
        float tile = 48f;
        return new MathBomberConfig(
                11, 13, tile,
                5, 6,
                20, 50,
                11, 20,
                30f, 3, 3,
                tile / 0.36f,
                0.36f,
                1.8f,
                0.20f,
                2,
                1, 2,
                Color.CYAN,
                "Hard"
        );
    }

}
