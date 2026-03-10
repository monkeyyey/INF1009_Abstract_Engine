package com.myGame.simulation.mathbomber.factory;

import com.myGame.engine.entities.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityFactoryRegistry {
    private final Map<Class<? extends Entity>, EntityFactory<? extends Entity, ?>> factories = new HashMap<>();

    public <T extends Entity, C> void registerFactory(Class<T> type, EntityFactory<T, C> factory) {
        factories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity, C> T createEntity(Class<T> type, C context) {
        EntityFactory<? extends Entity, ?> rawFactory = factories.get(type);
        if (rawFactory == null) {
            throw new IllegalArgumentException("Unknown entity type: " + type.getSimpleName());
        }
        EntityFactory<T, C> typedFactory = (EntityFactory<T, C>) rawFactory;
        return typedFactory.create(context);
    }
}
