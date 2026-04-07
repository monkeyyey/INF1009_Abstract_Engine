package com.myGame.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.collision.hitboxes.RectHitbox;

public class SquareMathEnemy extends MathEnemy {
    public SquareMathEnemy(int row, int col, int value, float tileSize, float boardX, float boardY) {
        super(row, col, value, tileSize, boardX, boardY, new RectHitbox(tileSize * 0.72f, tileSize * 0.72f));
    }

    @Override
    public void draw(ShapeRenderer shape) {
        float size = getSquareSize();
        shape.setColor(Color.SALMON);
        shape.rect(getX() - size * 0.5f, getY() - size * 0.5f, size, size);
    }
}
