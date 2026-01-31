package com.myGame.game.entities;

import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.MovableEntity;
import com.myGame.engine.entities.CircleHitbox;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MovableCircle extends MovableEntity {
    private float radius;

    public MovableCircle(float x, float y, float radius) {
        super(x, y);
        this.radius = radius;
        this.hitbox = new CircleHitbox(x, y, radius);
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.circle(x, y, radius);
    }

    @Override
    public void onCollision(Entity other) {
        // Game Logic
    }

    @Override
    public void dispose() {
        // No resources to dispose
    }

    public float getRadius() { return radius; }
}
