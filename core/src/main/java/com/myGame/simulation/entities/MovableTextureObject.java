package com.myGame.simulation.entities;

import com.myGame.engine.Collision.Hitboxes.Hitbox;
import com.myGame.engine.Collision.Hitboxes.RectHitbox;
import com.myGame.engine.Collision.Interfaces.iCollidable;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.MovementManagement.Interfaces.iMovable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import java.util.Objects;

public abstract class MovableTextureObject extends Entity implements iCollidable, iMovable {
    private final Texture texture;
    private final float width;
    private final float height;
    private Hitbox hitbox;
    private float vx;
    private float vy;

    public MovableTextureObject(String path, float x, float y, float width, float height) {
        super(x, y);
        Objects.requireNonNull(path, "Texture path cannot be null");
        if (path.isBlank()) {
            throw new IllegalArgumentException("Texture path cannot be blank");
        }
        if (width <= 0f || height <= 0f) {
            throw new IllegalArgumentException("Width and height must be > 0");
        }
        this.texture = new Texture(path);
        this.width = width;
        this.height = height;
        this.hitbox = new RectHitbox(width, height);
    }

    @Override
    public void updatePosition(float dt) {
        setX(getX() + vx * dt);
        setY(getY() + vy * dt);
    }

    public float getVelocityX() { return vx; }

    public void setVelocityX(float vx) { this.vx = vx; }

    public float getVelocityY() { return vy; }

    public void setVelocityY(float vy) { this.vy = vy; }

    @Override
    public Hitbox getHitbox() { return hitbox; }

    @Override
    public void setHitbox(Hitbox hitbox) {
        this.hitbox = Objects.requireNonNull(hitbox, "Hitbox cannot be null");
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, getX(), getY(), width, height);
    }

    public abstract void onCollision(Entity other);
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
