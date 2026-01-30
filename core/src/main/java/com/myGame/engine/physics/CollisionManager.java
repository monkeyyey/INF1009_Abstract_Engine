package com.myGame.engine.physics;

import com.myGame.engine.entities.Entity;
import java.util.List;

public class CollisionManager {
    private List<Entity> collidables;

    public void setCollidables(List<Entity> collidables) {
        this.collidables = collidables;
    }

    public void update(float dt) {
        if(collidables == null) return;
        for (int i = 0; i < collidables.size(); i++) {
            for (int j = i + 1; j < collidables.size(); j++) {
                Entity a = collidables.get(i);
                Entity b = collidables.get(j);
                // In real code, check CollisionDetector here
                resolve(a, b);
            }
        }
    }

    public void resolve(Entity a, Entity b) {
        a.onCollision(b);
        b.onCollision(a);
    }
}
