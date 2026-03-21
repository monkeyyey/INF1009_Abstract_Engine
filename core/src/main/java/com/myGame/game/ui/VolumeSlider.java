package com.myGame.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.entity.Entity;

public class VolumeSlider extends Entity {
    private final float width;
    private final float height;
    private final Color trackColor;
    private final Color fillColor;
    private float value;

    public VolumeSlider(float x, float y, float width, float height, Color trackColor, Color fillColor, float value) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.trackColor = trackColor;
        this.fillColor = fillColor;
        setValue(value);
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(trackColor);
        shape.rect(getX(), getY(), width, height);
        shape.setColor(fillColor);
        shape.rect(getX(), getY(), width * value, height);
    }

    @Override
    public void dispose() {
        // No resources.
    }

    public void setValue(float value) {
        this.value = Math.max(0f, Math.min(1f, value));
    }

    public float getValue() {
        return value;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean contains(float px, float py) {
        return px >= getX() && px <= getX() + width && py >= getY() && py <= getY() + height;
    }
}
