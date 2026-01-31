package com.myGame.engine.managers;

import com.myGame.engine.entities.Entity;
import com.myGame.engine.core.Collidable;
import com.myGame.engine.core.Movable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class EntityManager {
    private Map<String, Entity> entities = new HashMap<>();

    public void addEntity(String name, Entity entity) {
        entities.put(name, entity);
    }

    public void removeEntity(String name) {
        entities.remove(name);
    }

    public List<Collidable> getCollidableEntities() {
        List<Collidable> collidables = new ArrayList<>();
        for(Entity e : entities.values()) {
            if(e.isActive() && e instanceof Collidable) {
                Collidable c = (Collidable) e;
                if (c.getHitbox() != null) {
                    collidables.add(c);
                }
            }
        }
        return collidables;
    }

    public List<Movable> getMovableEntities() {
        List<Movable> movables = new ArrayList<>();
        for (Entity e : entities.values()) {
            if (e.isActive() && e instanceof Movable) {
                movables.add((Movable) e);
            }
        }
        return movables;
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

    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity e : entities.values()) {
            if (e.isActive()) {
                e.draw(batch, shape);
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
