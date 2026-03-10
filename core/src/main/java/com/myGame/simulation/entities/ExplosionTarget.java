package com.myGame.simulation.entities;

public interface ExplosionTarget {
    enum TargetType {
        PLAYER,
        ENEMY
    }

    TargetType getExplosionTargetType();
    void onExplosionHit();
}
