package com.myGame.game.entities;

import com.myGame.engine.core.Collidable;
import com.myGame.engine.core.Movable;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;
import com.myGame.engine.entities.RectHitbox;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public abstract class MovableTextureObject extends Entity implements Collidable, Movable {
    private final Texture texture;
    private final float width;
    private final float height;
    protected Hitbox hitbox;
    protected float vx;
    protected float vy;

    public MovableTextureObject(String path, float x, float y, float width, float height) {
        super(x, y);
        this.texture = new Texture(path);
        this.width = width;
        this.height = height;
        this.hitbox = new RectHitbox(width, height);
    }

    @Override
    public void updatePosition(float dt) {
        x += vx * dt;
        y += vy * dt;
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

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public abstract void onCollision(Entity other);
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
