package com.myGame.engine.collision.hitboxes;

public class RectHitbox extends Hitbox {
    private float width, height;

    public RectHitbox(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    @Override
    public boolean overlaps(float x, float y, Hitbox other, float otherX, float otherY) {
        return other.overlapsRect(otherX, otherY, this, x, y);
    }

    @Override
    protected boolean overlapsCircle(float x, float y, CircleHitbox circle, float circleX, float circleY) {
        float closestX = clamp(circleX, x, x + width);
        float closestY = clamp(circleY, y, y + height);
        float dx = circleX - closestX;
        float dy = circleY - closestY;
        return (dx * dx + dy * dy) <= (circle.getRadius() * circle.getRadius());
    }

    @Override
    protected boolean overlapsRect(float x, float y, RectHitbox rect, float rectX, float rectY) {
        return x < rectX + rect.getWidth()
                && x + width > rectX
                && y < rectY + rect.getHeight()
                && y + height > rectY;
    }
}
