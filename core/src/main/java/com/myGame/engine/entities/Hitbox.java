package com.myGame.engine.entities;

public abstract class Hitbox {
    protected float x, y;

    public Hitbox(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract boolean collidesWith(Hitbox other);

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
}
