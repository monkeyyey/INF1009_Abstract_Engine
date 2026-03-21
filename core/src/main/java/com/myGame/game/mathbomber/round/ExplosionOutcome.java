package com.myGame.game.mathbomber.round;

public class ExplosionOutcome {
    private boolean explosionTriggered;
    private boolean playerHit;
    private boolean correctEnemyHit;
    private boolean wrongEnemyHit;

    public void resetFrame() {
        explosionTriggered = false;
        playerHit = false;
        correctEnemyHit = false;
        wrongEnemyHit = false;
    }

    public void markExplosionTriggered() {
        explosionTriggered = true;
    }

    public void markPlayerHit() {
        playerHit = true;
    }

    public void markCorrectEnemyHit() {
        correctEnemyHit = true;
    }

    public void markWrongEnemyHit() {
        wrongEnemyHit = true;
    }

    public boolean isExplosionTriggered() {
        return explosionTriggered;
    }

    public boolean isPlayerHit() {
        return playerHit;
    }

    public boolean isCorrectEnemyHit() {
        return correctEnemyHit;
    }

    public boolean isWrongEnemyHit() {
        return wrongEnemyHit;
    }
}
