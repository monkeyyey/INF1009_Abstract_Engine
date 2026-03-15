package com.myGame.simulation.entities;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.InputManagement.Interfaces.InputState;

public class Bucket extends MovableTextureObject {
    private final float speed;
    private final float minX;
    private final float maxX;

    public Bucket(String path, float x, float y, float width, float height, float speed, float minX, float maxX) {
        super(path, x, y, width, height);
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;
    }

    public void applyInput(InputState input) {
        float vx = 0f;
        if (input.isLeft()) vx -= speed;
        if (input.isRight()) vx += speed;
        setVelocityX(vx);
        setVelocityY(0f);
    }

    @Override
    public void updatePosition(float dt) {
        super.updatePosition(dt);
        float clampedX = getX();
        if (clampedX < minX) clampedX = minX;
        if (clampedX > maxX) clampedX = maxX;
        if (clampedX != getX()) {
            setX(clampedX);
        }
    }

    @Override
    public void onCollision(Entity other) {
        // Bucket does not react for this demo
    }
}
