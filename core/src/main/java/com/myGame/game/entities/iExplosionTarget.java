package com.myGame.game.entities;

public interface iExplosionTarget {
    enum TargetType {
        PLAYER,
        ENEMY
    }

    TargetType getExplosionTargetType();
    void onExplosionHit();
}
