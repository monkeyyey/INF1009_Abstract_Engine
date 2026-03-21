package com.myGame.game.factory.support;

import com.myGame.engine.entity.Entity;

@FunctionalInterface
public interface EntityFactory<T extends Entity, C> {
    T create(C context);
}
