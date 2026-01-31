package com.myGame.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.InputState;
import com.myGame.engine.entities.Entity;

public class PlayerCircle extends MovableCircle {
    private final float speed;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    private final Color color;

    public PlayerCircle(float x, float y, float radius, float speed,
                        float minX, float maxX, float minY, float maxY, Color color) {
        super(x, y, radius);
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.color = color;
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
        float r = getRadius();
        float clampedX = getX();
        float clampedY = getY();
        if (clampedX < minX + r) clampedX = minX + r;
        if (clampedX > maxX - r) clampedX = maxX - r;
        if (clampedY < minY + r) clampedY = minY + r;
        if (clampedY > maxY - r) clampedY = maxY - r;
        if (clampedX != getX() || clampedY != getY()) {
            setX(clampedX);
            setY(clampedY);
            getHitbox().setX(clampedX);
            getHitbox().setY(clampedY);
        }
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(getX(), getY(), getRadius());
    }

    @Override
    public void onCollision(Entity other) {
        // No-op for player in this scene
    }
}
