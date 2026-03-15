package com.myGame.engine.Collision.Hitboxes;

public abstract class Hitbox {
    public abstract boolean overlaps(float x, float y, Hitbox other, float otherX, float otherY);

    protected abstract boolean overlapsCircle(float x, float y, CircleHitbox circle, float circleX, float circleY);

    protected abstract boolean overlapsRect(float x, float y, RectHitbox rect, float rectX, float rectY);

    protected static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
