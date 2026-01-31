package com.myGame.engine.entities;

import com.myGame.engine.entities.CircleHitbox;

public class RectHitbox extends Hitbox {
    private float width, height;

    public RectHitbox(float x, float y, float width, float height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean collidesWith(Hitbox other) {
        if (other instanceof RectHitbox) {
            return com.myGame.engine.physics.CollisionDetector.checkRectRect(this, (RectHitbox) other);
        }
        if (other instanceof CircleHitbox) {
            return com.myGame.engine.physics.CollisionDetector.checkCircleRect((CircleHitbox) other, this);
        }
        return false;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
