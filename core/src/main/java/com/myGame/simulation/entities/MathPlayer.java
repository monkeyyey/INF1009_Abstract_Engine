package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.InputState;
import com.myGame.engine.core.iCollidable;
import com.myGame.engine.core.iMovable;
import com.myGame.engine.entities.CircleHitbox;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;

import java.util.Objects;

public class MathPlayer extends Entity implements iMovable, iCollidable, ExplosionTarget {
    public interface OccupancyChecker {
        boolean canOccupy(float x, float y, float radius);
    }

    private int row;
    private int col;
    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private final float speed;
    private final Color color;

    private float velocityX;
    private float velocityY;
    private Hitbox hitbox;
    private OccupancyChecker occupancyChecker;
    private Runnable explosionHitHandler;

    public MathPlayer(int row, int col, float tileSize, float boardX, float boardY, float speed, Color color) {
        super(boardX + (col + 0.5f) * tileSize, boardY + (row + 0.5f) * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.speed = speed;
        this.color = color;
        this.hitbox = new CircleHitbox(getRadius());
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.velocityX = 0f;
        this.velocityY = 0f;
        setX(worldX(col));
        setY(worldY(row));
    }

    public void setOccupancyChecker(OccupancyChecker occupancyChecker) {
        this.occupancyChecker = occupancyChecker;
    }

    public void setExplosionHitHandler(Runnable explosionHitHandler) {
        this.explosionHitHandler = explosionHitHandler;
    }

    public void applyInput(InputState input) {
        float dx = 0f;
        float dy = 0f;
        if (input.isLeft()) dx -= 1f;
        if (input.isRight()) dx += 1f;
        if (input.isUp()) dy += 1f;
        if (input.isDown()) dy -= 1f;

        if (dx == 0f && dy == 0f) {
            velocityX = 0f;
            velocityY = 0f;
            return;
        }

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        velocityX = (dx / len) * speed;
        velocityY = (dy / len) * speed;
    }

    @Override
    public void updatePosition(float dt) {
        float radius = getRadius();

        float candidateX = getX() + velocityX * dt;
        if (occupancyChecker == null || occupancyChecker.canOccupy(candidateX, getY(), radius)) {
            setX(candidateX);
        }

        float candidateY = getY() + velocityY * dt;
        if (occupancyChecker == null || occupancyChecker.canOccupy(getX(), candidateY, radius)) {
            setY(candidateY);
        }

        syncGridFromWorld();
    }

    private void syncGridFromWorld() {
        this.col = Math.max(0, Math.round((getX() - boardX) / tileSize - 0.5f));
        this.row = Math.max(0, Math.round((getY() - boardY) / tileSize - 0.5f));
    }

    @Override
    public void onCollision(Entity other) {
        // Player collision responses are handled by the colliding entity.
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public void setHitbox(Hitbox hitbox) {
        Objects.requireNonNull(hitbox, "Hitbox cannot be null");
        if (!(hitbox instanceof CircleHitbox)) {
            throw new IllegalArgumentException("MathPlayer requires CircleHitbox");
        }
        this.hitbox = hitbox;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(getX(), getY(), getRadius());
    }

    @Override
    public void dispose() {
        // No resources.
    }

    @Override
    public TargetType getExplosionTargetType() {
        return TargetType.PLAYER;
    }

    @Override
    public void onExplosionHit() {
        if (explosionHitHandler != null) {
            explosionHitHandler.run();
        }
    }

    private float worldX(int col) {
        return boardX + (col + 0.5f) * tileSize;
    }

    private float worldY(int row) {
        return boardY + (row + 0.5f) * tileSize;
    }

    private float getRadius() {
        return tileSize * 0.33f;
    }
}
