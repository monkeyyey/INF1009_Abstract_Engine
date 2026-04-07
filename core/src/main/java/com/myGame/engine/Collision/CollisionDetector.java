package com.myGame.engine.collision;

import com.myGame.engine.collision.hitboxes.Hitbox;

public final class CollisionDetector {
    
    public static boolean overlaps(Hitbox a, float ax, float ay, Hitbox b, float bx, float by) {
        if (a == null || b == null) return false;
        return a.overlaps(ax, ay, b, bx, by);
    }
}
