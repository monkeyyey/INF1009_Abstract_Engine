package com.myGame.game.entities;

import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.MovableEntity;
import com.myGame.engine.entities.RectHitbox;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;

public class MovableTextureObject extends MovableEntity {
    private Texture tex;
    private float width, height;

    public MovableTextureObject(String path, float x, float y, float width, float height) {
        super(x, y);
        this.tex = new Texture(path);
        this.width = width;
        this.height = height;
        this.hitbox = new RectHitbox(x, y, width, height);
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        batch.draw(tex, x, y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
        // Game Logic
    }

    @Override
    public void dispose() {
        tex.dispose();
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
