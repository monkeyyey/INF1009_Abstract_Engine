package com.myGame.game.entities;

import com.myGame.engine.entities.Entity;
import com.myGame.engine.core.Movable;
import com.myGame.engine.entities.CircleHitbox;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MovableCircle extends Entity implements Movable {
    private float vx, vy;
    private float radius;

    public MovableCircle(float x, float y, float radius) {
        super(x, y);
        this.radius = radius;
        this.hitbox = new CircleHitbox(x, y, radius);
    }

    @Override
    public void updatePosition(float dt) {
        x += vx * dt;
        y += vy * dt;
        hitbox.setX(x);
        hitbox.setY(y);
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.circle(x, y, radius);
    }

    @Override
    public void onCollision(Entity other) {
        // Game Logic
    }

    // Movable Interface
    public float getVelocityX() { return vx; }
    public void setVelocityX(float vx) { this.vx = vx; }
    public float getVelocityY() { return vy; }
    public void setVelocityY(float vy) { this.vy = vy; }
}
