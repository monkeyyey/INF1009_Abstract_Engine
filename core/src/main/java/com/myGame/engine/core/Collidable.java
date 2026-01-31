package com.myGame.engine.core;

import com.myGame.engine.entities.Entity;
import com.myGame.engine.entities.Hitbox;

public interface Collidable {
    Hitbox getHitbox();
    void setHitbox(Hitbox hitbox);
    void onCollision(Entity other);
}
