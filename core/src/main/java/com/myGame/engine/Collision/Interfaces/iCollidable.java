package com.myGame.engine.Collision.Interfaces;

import com.myGame.engine.Collision.Hitboxes.Hitbox;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;

public interface iCollidable {
    Hitbox getHitbox();
    void setHitbox(Hitbox hitbox);
    void onCollision(Entity other);
}
