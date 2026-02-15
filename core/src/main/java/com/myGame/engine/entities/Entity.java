package com.myGame.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity {
    protected float x, y;
    protected String name; // null indicates unnamed
    protected boolean active = true;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(SpriteBatch batch) {
        // Default no-op for shape-only entities.
    }

    public void draw(ShapeRenderer shape) {
        // Default no-op for texture-only entities.
    }

    public abstract void dispose();

    // Getters and Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void destroy() { this.active = false; }
}
