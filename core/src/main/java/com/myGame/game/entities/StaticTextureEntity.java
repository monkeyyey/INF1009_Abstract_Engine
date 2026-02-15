package com.myGame.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.myGame.engine.entities.Entity;

public class StaticTextureEntity extends Entity {
    private final Texture texture;
    private final float width;
    private final float height;

    public StaticTextureEntity(String path, float x, float y, float width, float height) {
        super(x, y);
        this.texture = new Texture(path);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
