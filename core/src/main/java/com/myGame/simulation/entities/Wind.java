package com.myGame.simulation.entities;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.InputManagement.Interfaces.InputState;

public class Wind extends MovableTextureObject {
    private final float speed;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;

    public Wind(String path, float x, float y, float width, float height,
                float speed, float minX, float maxX, float minY, float maxY) {
        super(path, x, y, width, height);
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public void applyInput(InputState input) {
        float vx = 0f;
        float vy = 0f;
        if (input.isLeft()) vx -= speed;
        if (input.isRight()) vx += speed;
        if (input.isUp()) vy += speed;
        if (input.isDown()) vy -= speed;
        setVelocityX(vx);
        setVelocityY(vy);
    }

    @Override
    public void updatePosition(float dt) {
        super.updatePosition(dt);
        float clampedX = getX();
        float clampedY = getY();
        if (clampedX < minX) clampedX = minX;
        if (clampedX > maxX) clampedX = maxX;
        if (clampedY < minY) clampedY = minY;
        if (clampedY > maxY) clampedY = maxY;
        if (clampedX != getX() || clampedY != getY()) {
            setX(clampedX);
            setY(clampedY);
        }
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Droplet) {
            ((Droplet) other).applyWindCollision(this);
        }
    }
}
