package com.myGame.engine.Collision;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.EntityManagement.Interfaces.iTickable;

import java.util.Collection;

public class LifecycleManager {
    public void update(float dt, Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (!entity.isActive() || !(entity instanceof iTickable)) continue;
            ((iTickable) entity).tick(dt);
        }
    }
}
