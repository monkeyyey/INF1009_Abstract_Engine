package com.myGame.engine.physics;

import com.myGame.engine.entities.CircleHitbox;
import com.myGame.engine.entities.RectHitbox;

public class CollisionDetector {
    public static boolean checkCircleCircle(CircleHitbox a, CircleHitbox b) {
        float dx = a.getX() - b.getX();
        float dy = a.getY() - b.getY();
        float r = a.getRadius() + b.getRadius();
        return (dx * dx + dy * dy) <= (r * r);
    }

    public static boolean checkRectRect(RectHitbox a, RectHitbox b) {
        return a.getX() < b.getX() + b.getWidth()
            && a.getX() + a.getWidth() > b.getX()
            && a.getY() < b.getY() + b.getHeight()
            && a.getY() + a.getHeight() > b.getY();
    }

    public static boolean checkCircleRect(CircleHitbox a, RectHitbox b) {
        float closestX = clamp(a.getX(), b.getX(), b.getX() + b.getWidth());
        float closestY = clamp(a.getY(), b.getY(), b.getY() + b.getHeight());
        float dx = a.getX() - closestX;
        float dy = a.getY() - closestY;
        return (dx * dx + dy * dy) <= (a.getRadius() * a.getRadius());
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
