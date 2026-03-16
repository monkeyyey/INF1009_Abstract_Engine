package com.myGame.engine.Animation;

import com.myGame.engine.Animation.Interfaces.iAnimatable;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;

import java.util.Collection;

public class AnimationManager {
    public void update(float dt, Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;
        for (Entity entity : entities) {
            if (!entity.isActive() || !(entity instanceof iAnimatable)) continue;
            ((iAnimatable) entity).updateAnimation(dt);
        }
    }
}
