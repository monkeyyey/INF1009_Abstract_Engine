package com.myGame.engine.physics;

import com.myGame.engine.entities.CircleHitbox;
import com.myGame.engine.entities.Hitbox;
import com.myGame.engine.entities.RectHitbox;

public class CollisionDetector {
    public static boolean overlaps(Hitbox a, float ax, float ay, Hitbox b, float bx, float by) {
        if (a instanceof CircleHitbox && b instanceof CircleHitbox) {
            return checkCircleCircle((CircleHitbox) a, ax, ay, (CircleHitbox) b, bx, by);
        }
        if (a instanceof RectHitbox && b instanceof RectHitbox) {
            return checkRectRect((RectHitbox) a, ax, ay, (RectHitbox) b, bx, by);
        }
        if (a instanceof CircleHitbox && b instanceof RectHitbox) {
            return checkCircleRect((CircleHitbox) a, ax, ay, (RectHitbox) b, bx, by);
        }
        if (a instanceof RectHitbox && b instanceof CircleHitbox) {
            return checkCircleRect((CircleHitbox) b, bx, by, (RectHitbox) a, ax, ay);
        }
        return false;
    }

    public static boolean checkCircleCircle(CircleHitbox a, float ax, float ay,
                                            CircleHitbox b, float bx, float by) {
        float dx = ax - bx;
        float dy = ay - by;
        float r = a.getRadius() + b.getRadius();
        return (dx * dx + dy * dy) <= (r * r);
    }

    public static boolean checkRectRect(RectHitbox a, float ax, float ay,
                                        RectHitbox b, float bx, float by) {
        return ax < bx + b.getWidth()
            && ax + a.getWidth() > bx
            && ay < by + b.getHeight()
            && ay + a.getHeight() > by;
    }

    public static boolean checkCircleRect(CircleHitbox circle, float cx, float cy,
                                          RectHitbox rect, float rx, float ry) {
        float closestX = clamp(cx, rx, rx + rect.getWidth());
        float closestY = clamp(cy, ry, ry + rect.getHeight());
        float dx = cx - closestX;
        float dy = cy - closestY;
        return (dx * dx + dy * dy) <= (circle.getRadius() * circle.getRadius());
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
