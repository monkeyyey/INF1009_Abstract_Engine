package com.myGame.simulation.mathbomber.factory;

import com.myGame.engine.EntityManagement.AbstractEntities.Entity;

@FunctionalInterface
public interface EntityFactory<T extends Entity, C> {
    T create(C context);
}
