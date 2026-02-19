package com.myGame.engine.physics;

import com.myGame.engine.core.iMovable;
import com.myGame.engine.entities.Entity;
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
