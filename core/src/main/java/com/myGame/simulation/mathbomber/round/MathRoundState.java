package com.myGame.simulation.mathbomber.round;

public class MathRoundState {
    private final int winScore;
    private final int bombsPerQuestion;
    private final float questionTimeSeconds;

    private int score;
    private int bombsRemaining;
    private float questionTimeLeft;
    private String feedbackText = "";
    private float feedbackTimer;
    private boolean gameWon;

    public MathRoundState(int winScore, int bombsPerQuestion, float questionTimeSeconds) {
        this.winScore = winScore;
        this.bombsPerQuestion = bombsPerQuestion;
        this.questionTimeSeconds = questionTimeSeconds;
    }

    public void resetWholeGame() {
        score = 0;
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

    public int getScore() {
        return score;
    }

    public int getBombsRemaining() {
        return bombsRemaining;
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

    public float getFeedbackTimer() {
        return feedbackTimer;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setFeedback(String feedbackText, float feedbackTimer) {
        this.feedbackText = feedbackText;
        this.feedbackTimer = feedbackTimer;
    }
}
