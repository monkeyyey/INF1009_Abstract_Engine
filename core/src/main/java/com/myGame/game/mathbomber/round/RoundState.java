package com.myGame.game.mathbomber.round;

public class RoundState {
    private final int winScore;
    private final int bombsPerQuestion;
    private final float questionTimeSeconds;
    private final int maxLives;

    private int score;
    private int bombsRemaining;
    private int livesRemaining;
    private float questionTimeLeft;
    private String feedbackText = "";
    private float feedbackTimer;
    private boolean gameWon;

    public RoundState(int winScore, int bombsPerQuestion, float questionTimeSeconds, int maxLives) {
        this.winScore = winScore;
        this.bombsPerQuestion = bombsPerQuestion;
        this.questionTimeSeconds = questionTimeSeconds;
        this.maxLives = maxLives;
    }

    public void resetWholeGame() {
        score = 0;
        livesRemaining = maxLives;
        gameWon = false;
        feedbackText = "";
        feedbackTimer = 0f;
    }

    public void startQuestion(String feedback, float feedbackSeconds, boolean applyScorePenalty) {
        if (applyScorePenalty) {
            score = Math.max(0, score - 1);
        }
        if (feedback != null) {
            feedbackText = feedback;
            feedbackTimer = feedbackSeconds;
        }
        bombsRemaining = bombsPerQuestion;
        questionTimeLeft = questionTimeSeconds;
    }

    public void tick(float dt) {
        questionTimeLeft -= dt;
        if (feedbackTimer > 0f) {
            feedbackTimer -= dt;
            if (feedbackTimer <= 0f) {
                feedbackText = "";
            }
        }
    }

    public boolean consumeBomb() {
        if (bombsRemaining <= 0) {
            return false;
        }
        bombsRemaining--;
        return true;
    }

    public boolean markCorrectAnswer() {
        score = Math.min(winScore, score + 1);
        gameWon = score >= winScore;
        return gameWon;
    }

    public boolean consumeLife() {
        if (livesRemaining <= 0) {
            return false;
        }
        livesRemaining--;
        return livesRemaining > 0;
    }

    public int getScore() {
        return score;
    }

    public int getBombsRemaining() {
        return bombsRemaining;
    }

    public int getLivesRemaining() {
        return livesRemaining;
    }

    public float getQuestionTimeLeft() {
        return questionTimeLeft;
    }

    public boolean isTimeUp() {
        return questionTimeLeft <= 0f;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setFeedback(String feedbackText, float feedbackTimer) {
        this.feedbackText = feedbackText;
        this.feedbackTimer = feedbackTimer;
    }
}
