package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.iCollidable;
import com.myGame.engine.core.InputState;
import com.myGame.engine.core.iMovable;
import com.myGame.engine.entities.CircleHitbox;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;
import java.util.Objects;

public class PlayerCircle extends Entity implements iCollidable, iMovable {
    private final float radius;
    private final float speed;
    private final float minX;
    private final float maxX;
    private final float minY;
    private final float maxY;
    private Color color;
    private Hitbox hitbox;
    private float vx;
    private float vy;
    private float previousX;
    private float previousY;

    public PlayerCircle(float x, float y, float radius, float speed,
                        float minX, float maxX, float minY, float maxY, Color color) {
        super(x, y);
        this.radius = radius;
        this.speed = speed;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.color = color;
        this.hitbox = new CircleHitbox(radius);
    }

    public void applyInput(InputState input) {
        float vx = 0f;
        float vy = 0f;
        if (input.isLeft()) vx -= speed;
        if (input.isRight()) vx += speed;
        if (input.isUp()) vy += speed;
        if (input.isDown()) vy -= speed;
        setVelocityX(vx);
        setVelocityY(vy);
    }

    @Override
    public void updatePosition(float dt) {
        previousX = getX();
        previousY = getY();
        setX(getX() + vx * dt);
        setY(getY() + vy * dt);
        float r = getRadius();
        float clampedX = getX();
        float clampedY = getY();
        if (clampedX < minX + r) clampedX = minX + r;
        if (clampedX > maxX - r) clampedX = maxX - r;
        if (clampedY < minY + r) clampedY = minY + r;
        if (clampedY > maxY - r) clampedY = maxY - r;
        if (clampedX != getX() || clampedY != getY()) {
            setX(clampedX);
            setY(clampedY);
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(getX(), getY(), getRadius());
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof RectangleWall) {
            setX(previousX);
            setY(previousY);
            setVelocityX(0f);
            setVelocityY(0f);
        } else if (other instanceof PlayerCircle) {
            setColor(Color.WHITE);
            ((PlayerCircle) other).setColor(Color.WHITE);
        }
    }

    @Override
    public Hitbox getHitbox() { return hitbox; }

    @Override
    public void setHitbox(Hitbox hitbox) {
        Objects.requireNonNull(hitbox, "Hitbox cannot be null");
        if (!(hitbox instanceof CircleHitbox)) {
            throw new IllegalArgumentException("PlayerCircle requires CircleHitbox");
        }
        this.hitbox = hitbox;
    }

    public float getVelocityX() { return vx; }

    public void setVelocityX(float vx) { this.vx = vx; }

    public float getVelocityY() { return vy; }

    public void setVelocityY(float vy) { this.vy = vy; }

    public float getRadius() { return radius; }

    public void setColor(Color color) {
        this.color = Objects.requireNonNull(color, "Color cannot be null");
    }

    @Override
    public void dispose() {
        // No resources to dispose.
    }
}
