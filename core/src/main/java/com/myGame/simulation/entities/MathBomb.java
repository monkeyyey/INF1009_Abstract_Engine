package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.iMovable;
import com.myGame.engine.entities.Entity;

public class MathBomb extends Entity implements iMovable {
    private final int row;
    private final int col;
    private final float tileSize;
    private final float fuseSeconds;
    private final int blastRange;
    private float timer;

    public MathBomb(int row, int col, float tileSize, float boardX, float boardY, float fuseSeconds, int blastRange) {
        super(boardX + col * tileSize, boardY + row * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.fuseSeconds = fuseSeconds;
        this.blastRange = blastRange;
        this.timer = fuseSeconds;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getBlastRange() {
        return blastRange;
    }

    @Override
    public void updatePosition(float dt) {
        timer -= dt;
    }

    public boolean isExpired() {
        return timer <= 0f;
    }

    public float getFuseProgress01() {
        return Math.max(0f, Math.min(1f, 1f - (timer / fuseSeconds)));
    }

    @Override
    public void draw(ShapeRenderer shape) {
        float cx = getX() + tileSize * 0.5f;
        float cy = getY() + tileSize * 0.5f;
        float radius = tileSize * (0.22f + 0.08f * getFuseProgress01());
        shape.setColor(Color.BLACK);
        shape.circle(cx, cy, radius);
    }

    @Override
    public void dispose() {
        // No resources.
    }
}
