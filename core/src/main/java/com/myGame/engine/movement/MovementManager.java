package com.myGame.engine.movement;

import com.myGame.engine.entity.Entity;

import java.util.Collection;

public class MovementManager {
    public void update(float dt, Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (!entity.isActive() || !(entity instanceof iMovable)) continue;
            ((iMovable) entity).updatePosition(dt);
        }
    }
}
