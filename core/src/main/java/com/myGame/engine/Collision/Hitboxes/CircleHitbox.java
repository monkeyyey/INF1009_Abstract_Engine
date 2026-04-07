package com.myGame.engine.collision.hitboxes;

public class CircleHitbox extends Hitbox {
    private float radius;

    public CircleHitbox(float radius) {
        this.radius = radius;
    }

    public float getRadius() { return radius; }

    @Override
    public boolean overlaps(float x, float y, Hitbox other, float otherX, float otherY) {
        return other.overlapsCircle(otherX, otherY, this, x, y);
    }

    @Override
    protected boolean overlapsCircle(float x, float y, CircleHitbox circle, float circleX, float circleY) {
        float dx = x - circleX;
        float dy = y - circleY;
        float r = radius + circle.getRadius();
        return (dx * dx + dy * dy) <= (r * r);
    }

    @Override
    protected boolean overlapsRect(float x, float y, RectHitbox rect, float rectX, float rectY) {
        float closestX = clamp(x, rectX, rectX + rect.getWidth());
        float closestY = clamp(y, rectY, rectY + rect.getHeight());
        float dx = x - closestX;
        float dy = y - closestY;
        return (dx * dx + dy * dy) <= (radius * radius);
    }
}
