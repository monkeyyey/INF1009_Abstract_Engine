package com.myGame.engine.core;

public interface Movable {
    void updatePosition(float dt);
    float getVelocityX();
    void setVelocityX(float vx);
    float getVelocityY();
    void setVelocityY(float vy);
}
