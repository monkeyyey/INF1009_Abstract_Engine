package com.myGame.game.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.collision.iCollidable;
import com.myGame.engine.collision.Hitboxes.Hitbox;
import com.myGame.engine.entity.Entity;
import com.myGame.engine.movement.iMovable;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class MathEnemy extends Entity implements iMovable, iCollidable, iExplosionTarget {
    private int row;
    private int col;
    private final int value;
    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private boolean moving;
    private float moveElapsed;
    private float moveDuration;
    private float startX;
    private float startY;
    private float targetX;
    private float targetY;
    private int lastDirRow;
    private int lastDirCol;
    private Hitbox hitbox;
    private Consumer<MathEnemy> explosionHitHandler;
    private Runnable playerCollisionHandler;

    protected MathEnemy(int row, int col, int value, float tileSize, float boardX, float boardY, Hitbox hitbox) {
        super(boardX + (col + 0.5f) * tileSize, boardY + (row + 0.5f) * tileSize);
        this.row = row;
        this.col = col;
        this.value = value;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.hitbox = Objects.requireNonNull(hitbox, "Hitbox cannot be null");
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }

    public void startMoveTo(int row, int col, float duration) {
        this.row = row;
        this.col = col;
        this.moving = true;
        this.moveElapsed = 0f;
        this.moveDuration = Math.max(0.001f, duration);
        this.startX = getX();
        this.startY = getY();
        this.targetX = worldX(col);
        this.targetY = worldY(row);
    }

    @Override
    public void updatePosition(float dt) {
        if (!moving) return;
        moveElapsed += dt;
        float t = Math.min(1f, moveElapsed / moveDuration);
        setX(startX + (targetX - startX) * t);
        setY(startY + (targetY - startY) * t);
        if (t >= 1f) {
            moving = false;
        }
    }

    public boolean isMoving() {
        return moving;
    }

    public int getLastDirRow() {
        return lastDirRow;
    }

    public int getLastDirCol() {
        return lastDirCol;
    }

    public void setLastDirection(int dirRow, int dirCol) {
        this.lastDirRow = dirRow;
        this.lastDirCol = dirCol;
    }

    public void setExplosionHitHandler(Consumer<MathEnemy> explosionHitHandler) {
        this.explosionHitHandler = explosionHitHandler;
    }

    public void setPlayerCollisionHandler(Runnable playerCollisionHandler) {
        this.playerCollisionHandler = playerCollisionHandler;
    }

    @Override
    public void onCollision(Entity other) {
        if (!isActive()) return;
        if (other instanceof MathPlayer && playerCollisionHandler != null) {
            playerCollisionHandler.run();
        }
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public void setHitbox(Hitbox hitbox) {
        this.hitbox = Objects.requireNonNull(hitbox, "Hitbox cannot be null");
    }

    @Override
    public void dispose() {
        // No resources.
    }

    @Override
    public TargetType getExplosionTargetType() {
        return TargetType.ENEMY;
    }

    @Override
    public void onExplosionHit() {
        if (!isActive()) return;
        if (explosionHitHandler != null) {
            explosionHitHandler.accept(this);
        }
        destroy();
    }

    protected float worldX(int col) {
        return boardX + (col + 0.5f) * tileSize;
    }

    protected float worldY(int row) {
        return boardY + (row + 0.5f) * tileSize;
    }

    protected float getRadius() {
        return tileSize * 0.36f;
    }

    protected float getSquareSize() {
        return tileSize * 0.72f;
    }
}
