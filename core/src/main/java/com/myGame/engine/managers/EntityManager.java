package com.myGame.engine.managers;

import com.myGame.engine.entities.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

public class EntityManager {
    private final Map<String, Entity> entities = new LinkedHashMap<>();

    public void addEntity(String name, Entity entity) {
        entity.setName(name);
        entities.put(name, entity);
    }

    public void removeEntity(String name) {
        entities.remove(name);
    }

    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }

    public void update(float dt) {
        Iterator<Map.Entry<String, Entity>> it = entities.entrySet().iterator();
        while (it.hasNext()) {
            Entity e = it.next().getValue();
            if (!e.isActive()) {
                e.dispose();
                it.remove();
            }
        }
    }

    public void drawSprites(SpriteBatch batch) {
        for (Entity e : entities.values()) {
            if (e.isActive()) {
                e.draw(batch);
            }
        }
    }

    public void drawShapes(ShapeRenderer shape) {
        for (Entity e : entities.values()) {
            if (e.isActive()) {
                e.draw(shape);
            }
        }
    }

    public void dispose() {
        for (Entity e : entities.values()) {
            e.dispose();
        }
        entities.clear();
    }
}
