package com.myGame.engine.entities;

public class CircleHitbox extends Hitbox {
    private float radius;

    public CircleHitbox(float radius) {
        this.radius = radius;
    }

    public float getRadius() { return radius; }
}
