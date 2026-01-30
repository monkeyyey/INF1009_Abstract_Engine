package com.myGame.engine.entities;

public class CircleHitbox extends Hitbox {
    private float radius;

    public CircleHitbox(float x, float y, float radius) {
        super(x, y);
        this.radius = radius;
    }

    @Override
    public boolean collidesWith(Hitbox other) {
        // Placeholder
        return false;
    }

    public float getRadius() { return radius; }
}
