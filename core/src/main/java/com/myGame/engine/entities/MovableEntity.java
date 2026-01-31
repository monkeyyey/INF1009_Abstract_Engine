package com.myGame.engine.entities;

import com.myGame.engine.core.Collidable;
import com.myGame.engine.core.Movable;

public abstract class MovableEntity extends Entity implements Collidable, Movable {
    protected Hitbox hitbox;
    protected float vx;
    protected float vy;

    public MovableEntity(float x, float y) {
        super(x, y);
    }

    @Override
    public void updatePosition(float dt) {
        x += vx * dt;
        y += vy * dt;
        if (hitbox != null) {
            hitbox.setX(x);
            hitbox.setY(y);
        }
    }

    @Override
    public float getVelocityX() { return vx; }

    @Override
    public void setVelocityX(float vx) { this.vx = vx; }

    @Override
    public float getVelocityY() { return vy; }

    @Override
    public void setVelocityY(float vy) { this.vy = vy; }

    @Override
    public Hitbox getHitbox() { return hitbox; }

    @Override
    public void setHitbox(Hitbox hitbox) { this.hitbox = hitbox; }
}
