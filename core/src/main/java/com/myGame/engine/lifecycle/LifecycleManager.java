package com.myGame.engine.lifecycle;

import java.util.Collection;

import com.myGame.engine.entity.Entity;

public class LifecycleManager {
    public void update(float dt, Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (!entity.isActive() || !(entity instanceof iTickable)) continue;
            ((iTickable) entity).tick(dt);
        }
    }
}
