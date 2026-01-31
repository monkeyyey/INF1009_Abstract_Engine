package com.myGame.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity {
    protected float x, y;
    protected int id = -1; // -1 indicates unowned
    protected boolean active = true;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(SpriteBatch batch, ShapeRenderer shape);
    public abstract void onCollision(Entity other);
    public abstract void dispose();

    // Getters and Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isActive() { return active; }
    public void destroy() { this.active = false; }
}
