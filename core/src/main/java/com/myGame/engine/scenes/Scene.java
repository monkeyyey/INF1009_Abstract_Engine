package com.myGame.engine.scenes;

import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Scene {
    protected final EntityManager entityManager;
    protected final CollisionManager collisionManager;
    protected final MovementManager movementManager;

    public Scene() {
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        movementManager = new MovementManager();
    }

    public abstract void onEnter();
    public abstract void onExit();
    
    public void update(float dt) {
        movementManager.update(dt, entityManager.getEntities());
        collisionManager.update(entityManager.getEntities());
        entityManager.update(dt);
    }

    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
    public abstract void dispose();
}
