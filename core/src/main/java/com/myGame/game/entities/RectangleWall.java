package com.myGame.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.Collidable;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;
import com.myGame.engine.entities.RectHitbox;

public class RectangleWall extends Entity implements Collidable {
    private RectHitbox hitbox;
    private final float width;
    private final float height;
    private Color color;

    public RectangleWall(float x, float y, float width, float height, Color color) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = color;
        this.hitbox = new RectHitbox(x, y, width, height);
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(x, y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
        // Color handled by scene logic
    }

    @Override
    public void dispose() {
        // No resources
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }

    @Override
    public RectHitbox getHitbox() { return hitbox; }

    @Override
    public void setHitbox(Hitbox hitbox) {
        if (hitbox instanceof RectHitbox) {
            this.hitbox = (RectHitbox) hitbox;
        }
    }
}
