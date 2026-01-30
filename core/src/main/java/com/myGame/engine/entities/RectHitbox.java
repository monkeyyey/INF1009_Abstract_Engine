package com.myGame.engine.entities;

public class RectHitbox extends Hitbox {
    private float width, height;

    public RectHitbox(float x, float y, float width, float height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean collidesWith(Hitbox other) {
        // Placeholder: actual logic delegates to CollisionDetector
        return false;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
