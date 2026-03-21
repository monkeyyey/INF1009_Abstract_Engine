package com.myGame.game.mathbomber.configurations;

public class MathBomberGameStats {
    private final String difficultyName;
    private final float elapsedSeconds;
    private final int score;

    public MathBomberGameStats(String difficultyName, float elapsedSeconds, int score) {
        this.difficultyName = difficultyName;
        this.elapsedSeconds = elapsedSeconds;
        this.score = score;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public float getElapsedSeconds() {
        return elapsedSeconds;
    }

    public int getScore() {
        return score;
    }
}
