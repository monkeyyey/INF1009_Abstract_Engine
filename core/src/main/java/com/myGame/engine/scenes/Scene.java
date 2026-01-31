package com.myGame.engine.scenes;

import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Scene {
    protected EntityManager entityManager;
    protected CollisionManager collisionManager;
    protected MovementManager movementManager;

    public Scene() {
        entityManager = new EntityManager();
        collisionManager = new CollisionManager();
        movementManager = new MovementManager();
    }

    public abstract void onEnter();
    public abstract void onExit();
    
    public void update(float dt) {
        collisionManager.setCollidables(entityManager.getCollidableEntities());
        movementManager.setMovables(entityManager.getMovableEntities());
        movementManager.update(dt);
        collisionManager.update(dt);
        entityManager.update(dt);
    }

    public abstract void render(SpriteBatch batch, ShapeRenderer shape);
    public abstract void dispose();
}
