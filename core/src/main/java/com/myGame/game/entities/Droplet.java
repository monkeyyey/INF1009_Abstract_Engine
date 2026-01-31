package com.myGame.game.entities;

import com.myGame.engine.entities.Entity;

public class Droplet extends MovableTextureObject {
    private boolean caught = false;

    public Droplet(String path, float x, float y, float width, float height) {
        super(path, x, y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Bucket) {
            caught = true;
        }
    }

    public boolean isCaught() {
        return caught;
    }

    public void reset(float x, float y, float fallSpeed) {
        setX(x);
        setY(y);
        getHitbox().setX(x);
        getHitbox().setY(y);
        setVelocityX(0f);
        setVelocityY(fallSpeed);
        caught = false;
    }
}
