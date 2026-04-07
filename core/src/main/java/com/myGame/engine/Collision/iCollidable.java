package com.myGame.engine.collision;

import com.myGame.engine.collision.hitboxes.Hitbox;
import com.myGame.engine.entity.Entity;

public interface iCollidable {
    Hitbox getHitbox();
    void setHitbox(Hitbox hitbox);
    void onCollision(Entity other);
}
