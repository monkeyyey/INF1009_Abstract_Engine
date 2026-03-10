package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.iCollidable;
import com.myGame.engine.core.iMovable;
import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;
import com.myGame.engine.entities.RectHitbox;

import java.util.Objects;

public class ExplosionCell extends Entity implements iMovable, iCollidable {
    private final int row;
    private final int col;
    private final float tileSize;
    private float timeLeft;
    private Hitbox hitbox;

    public ExplosionCell(int row, int col, float tileSize, float boardX, float boardY, float timeLeft) {
        super(boardX + col * tileSize, boardY + row * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.timeLeft = timeLeft;
        this.hitbox = new RectHitbox(tileSize, tileSize);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public void updatePosition(float dt) {
        timeLeft -= dt;
    }

    public boolean isExpired() {
        return timeLeft <= 0f;
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public void setHitbox(Hitbox hitbox) {
        Objects.requireNonNull(hitbox, "Hitbox cannot be null");
        if (!(hitbox instanceof RectHitbox)) {
            throw new IllegalArgumentException("ExplosionCell requires RectHitbox");
        }
        this.hitbox = hitbox;
    }

    @Override
    public void onCollision(Entity other) {
        if (!isActive()) return;
        if (other instanceof ExplosionTarget) {
            ((ExplosionTarget) other).onExplosionHit();
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        float pad = tileSize * 0.1f;
        shape.setColor(Color.ORANGE);
        shape.rect(getX() + pad, getY() + pad, tileSize - 2f * pad, tileSize - 2f * pad);
    }

    @Override
    public void dispose() {
        // No resources.
    }
}
