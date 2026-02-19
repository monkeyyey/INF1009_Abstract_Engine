package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.myGame.engine.entities.Entity;
import java.util.Objects;

public class StaticTextureEntity extends Entity {
    private final Texture texture;
    private final float width;
    private final float height;

    public StaticTextureEntity(String path, float x, float y, float width, float height) {
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
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, getX(), getY(), width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
