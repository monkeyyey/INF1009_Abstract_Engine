package com.myGame.simulation.interfaces;

public interface ExplosionTarget {
    enum TargetType {
        PLAYER,
        ENEMY
    }

    TargetType getExplosionTargetType();
    void onExplosionHit();
}
