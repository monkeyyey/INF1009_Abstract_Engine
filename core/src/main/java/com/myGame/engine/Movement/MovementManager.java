package com.myGame.engine.Movement;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.Movement.Interfaces.iMovable;

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
