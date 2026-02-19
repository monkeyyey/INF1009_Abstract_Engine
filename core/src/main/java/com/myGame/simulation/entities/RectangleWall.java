package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.iCollidable;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;
import com.myGame.engine.entities.RectHitbox;
import java.util.Objects;

public class RectangleWall extends Entity implements iCollidable {
    private RectHitbox hitbox;
    private final float width;
    private final float height;
    private Color color;

    public RectangleWall(float x, float y, float width, float height, Color color) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = color;
        this.hitbox = new RectHitbox(width, height);
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(getX(), getY(), width, height);
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof PlayerCircle) {
            setColor(Color.RED);
        }
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
        Objects.requireNonNull(hitbox, "Hitbox cannot be null");
        if (!(hitbox instanceof RectHitbox)) {
            throw new IllegalArgumentException("RectangleWall requires RectHitbox");
        }
        this.hitbox = (RectHitbox) hitbox;
    }
}
