package com.myGame.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.collision.Hitboxes.CircleHitbox;

public class CircleMathEnemy extends MathEnemy {
    public CircleMathEnemy(int row, int col, int value, float tileSize, float boardX, float boardY) {
        super(row, col, value, tileSize, boardX, boardY, new CircleHitbox(tileSize * 0.36f));
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.SALMON);
        shape.circle(getX(), getY(), getRadius());
    }
}
