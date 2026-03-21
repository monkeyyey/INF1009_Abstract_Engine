package com.myGame.engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.entity.EntityManager;

import java.util.Objects;

public abstract class Scene {
    protected final EntityManager entityManager;

    protected Scene(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
    }

    public abstract void onEnter();
    public abstract void onExit();
    
    public void update(float dt) {
        entityManager.update(dt);
    }

    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
    public abstract void dispose();
}
