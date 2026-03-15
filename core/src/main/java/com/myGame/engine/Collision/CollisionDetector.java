package com.myGame.engine.Collision;

import com.myGame.engine.Collision.Hitboxes.Hitbox;

public final class CollisionDetector {
    
    public static boolean overlaps(Hitbox a, float ax, float ay, Hitbox b, float bx, float by) {
        if (a == null || b == null) return false;
        return a.overlaps(ax, ay, b, bx, by);
    }
}
