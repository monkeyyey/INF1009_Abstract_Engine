package com.myGame.simulation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.entities.Entity;

public class Button extends Entity {
    private final String label;
    private final float width;
    private final float height;
    private Color color;
    private final Runnable onClick;

    public Button(String label, float x, float y, float width, float height, Color color, Runnable onClick) {
        super(x, y);
        this.label = label;
        this.width = width;
        this.height = height;
        this.color = color;
        this.onClick = onClick;
    }

    public boolean contains(float px, float py) {
        return px >= getX() && px <= getX() + width && py >= getY() && py <= getY() + height;
    }

    public boolean handleClick(float px, float py) {
        if (!contains(px, py)) return false;
        if (onClick != null) onClick.run();
        return true;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.rect(getX(), getY(), width, height);
    }

    @Override
    public void dispose() {
        // No resources.
    }

    public String getLabel() {
        return label;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
