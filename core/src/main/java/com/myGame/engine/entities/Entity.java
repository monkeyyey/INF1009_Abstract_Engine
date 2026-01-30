package com.myGame.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity {
    protected float x, y;
    protected Hitbox hitbox;
    protected int id = -1; // -1 indicates unowned

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(SpriteBatch batch, ShapeRenderer shape);
    public abstract void onCollision(Entity other);

    // Getters and Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public Hitbox getHitbox() { return hitbox; }
    public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
