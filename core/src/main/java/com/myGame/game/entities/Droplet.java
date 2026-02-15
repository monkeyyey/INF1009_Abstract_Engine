package com.myGame.game.entities;

import com.myGame.engine.entities.Entity;

public class Droplet extends MovableTextureObject {
    private boolean caught = false;
    private float fallSpeed = -200f;
    private float windPushSpeed = 140f;
    private boolean touchedByWindThisFrame = false;

    public Droplet(String path, float x, float y, float width, float height, float fallSpeed, float windPushSpeed) {
        super(path, x, y, width, height);
        this.fallSpeed = fallSpeed;
        this.windPushSpeed = windPushSpeed;
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Bucket) {
            caught = true;
        }
    }

    public void beginFrame() {
        touchedByWindThisFrame = false;
    }

    public void applyWindCollision(Wind wind) {
        touchedByWindThisFrame = true;
        float windTop = wind.getY() + wind.getHeight();
        boolean xOverlap = getX() < wind.getX() + wind.getWidth()
                && getX() + getWidth() > wind.getX();
        boolean onTop = xOverlap && Math.abs(getY() - windTop) <= 3f;

        if (onTop) {
            setVelocityX(0f);
            setVelocityY(0f);
            return;
        }

        float dCenterX = getX() + getWidth() / 2f;
        float wCenterX = wind.getX() + wind.getWidth() / 2f;
        setVelocityY(fallSpeed);
        if (dCenterX < wCenterX) {
            setVelocityX(-windPushSpeed);
        } else {
            setVelocityX(windPushSpeed);
        }
    }

    public void endFrame() {
        if (!touchedByWindThisFrame && !caught) {
            setVelocityX(0f);
            setVelocityY(fallSpeed);
        }
    }

    public boolean isCaught() {
        return caught;
    }

    public void reset(float x, float y, float fallSpeed) {
        setX(x);
        setY(y);
        this.fallSpeed = fallSpeed;
        setVelocityX(0f);
        setVelocityY(fallSpeed);
        caught = false;
    }
}
