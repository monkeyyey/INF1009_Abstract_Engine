package com.myGame.engine.entities;

import com.myGame.engine.entities.RectHitbox;

public class CircleHitbox extends Hitbox {
    private float radius;

    public CircleHitbox(float x, float y, float radius) {
        super(x, y);
        this.radius = radius;
    }

    @Override
    public boolean collidesWith(Hitbox other) {
        if (other instanceof CircleHitbox) {
            return com.myGame.engine.physics.CollisionDetector.checkCircleCircle(this, (CircleHitbox) other);
        }
        if (other instanceof RectHitbox) {
            return com.myGame.engine.physics.CollisionDetector.checkCircleRect(this, (RectHitbox) other);
        }
        return false;
    }

    public float getRadius() { return radius; }
}
