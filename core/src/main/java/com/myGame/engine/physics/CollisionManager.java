package com.myGame.engine.physics;

import com.myGame.engine.core.Collidable;
import com.myGame.engine.entities.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollisionManager {
    public void update(Collection<Entity> entities) {
        if (entities == null || entities.isEmpty()) return;

        List<Entity> all = new ArrayList<>(entities);
        for (int i = 0; i < all.size(); i++) {
            Entity ea = all.get(i);
            if (!ea.isActive() || !(ea instanceof Collidable)) continue;

            Collidable a = (Collidable) ea;
            if (a.getHitbox() == null) continue;

            for (int j = i + 1; j < all.size(); j++) {
                Entity eb = all.get(j);
                if (!eb.isActive() || !(eb instanceof Collidable)) continue;

                Collidable b = (Collidable) eb;
                if (b.getHitbox() == null) continue;

                if (CollisionDetector.overlaps(
                        a.getHitbox(), ea.getX(), ea.getY(),
                        b.getHitbox(), eb.getX(), eb.getY())) {
                    resolve(a, ea, b, eb);
                }
            }
        }
    }

    private void resolve(Collidable a, Entity ea, Collidable b, Entity eb) {
        a.onCollision(eb);
        b.onCollision(ea);
    }
}
