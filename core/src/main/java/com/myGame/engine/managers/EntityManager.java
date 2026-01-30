package com.myGame.engine.managers;

import com.myGame.engine.entities.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class EntityManager {
    private Map<String, Entity> entities = new HashMap<>();

    public void addEntity(String name, Entity entity) {
        entities.put(name, entity);
    }

    public void removeEntity(String name) {
        entities.remove(name);
    }

    public List<Entity> getCollidableEntities() {
        List<Entity> collidables = new ArrayList<>();
        for(Entity e : entities.values()) {
            if(e.getHitbox() != null) {
                collidables.add(e);
            }
        }
        return collidables;
    }

    public void update(float dt) {
        // Update logic
    }
}
