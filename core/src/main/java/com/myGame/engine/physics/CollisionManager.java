package com.myGame.engine.physics;

import com.myGame.engine.core.Collidable;
import com.myGame.engine.entities.Entity;
import java.util.List;

public class CollisionManager {
    private List<Collidable> collidables;

    public void setCollidables(List<Collidable> collidables) {
        this.collidables = collidables;
    }

    public void update(float dt) {
        if(collidables == null) return;
        for (int i = 0; i < collidables.size(); i++) {
            for (int j = i + 1; j < collidables.size(); j++) {
                Collidable a = collidables.get(i);
                Collidable b = collidables.get(j);
                Entity ea = (Entity) a;
                Entity eb = (Entity) b;
                if (!ea.isActive() || !eb.isActive()) continue;
                if (a.getHitbox() != null && b.getHitbox() != null
                        && a.getHitbox().collidesWith(b.getHitbox())) {
                    resolve(ea, eb);
                }
            }
        }
    }

    public void resolve(Entity a, Entity b) {
        a.onCollision(b);
        b.onCollision(a);
    }
}
