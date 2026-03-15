package com.myGame.simulation.entities;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;

public class Droplet extends MovableTextureObject {
    private boolean caught = false;
    private float fallSpeed = -200f;
    private boolean touchedByWindThisFrame = false;
    private final Runnable onCaught;

    public Droplet(String path, float x, float y, float width, float height, float fallSpeed) {
        this(path, x, y, width, height, fallSpeed, null);
    }

    public Droplet(String path, float x, float y, float width, float height,
                   float fallSpeed, Runnable onCaught) {
        super(path, x, y, width, height);
        this.fallSpeed = fallSpeed;
        this.onCaught = onCaught;
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Bucket && !caught) {
            caught = true;
            if (onCaught != null) {
                onCaught.run();
            }
        }
    }

    public void beginFrame() {
        touchedByWindThisFrame = false;
    }

    public void applyWindCollision(Wind wind) {
        touchedByWindThisFrame = true;
        setVelocityY(fallSpeed);
        setVelocityX(wind.getVelocityX());
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
