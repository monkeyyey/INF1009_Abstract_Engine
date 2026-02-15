package com.myGame.engine.entities;

public class RectHitbox extends Hitbox {
    private float width, height;

    public RectHitbox(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
