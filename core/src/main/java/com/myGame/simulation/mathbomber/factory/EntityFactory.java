package com.myGame.simulation.mathbomber.factory;

import com.myGame.engine.entities.Entity;

@FunctionalInterface
public interface EntityFactory<T extends Entity, C> {
    T create(C context);
}
