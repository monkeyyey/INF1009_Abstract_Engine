package com.myGame.engine.scenes;

import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.Objects;

public abstract class Scene {
    protected final EntityManager entityManager;
    protected final CollisionManager collisionManager;
    protected final MovementManager movementManager;

    protected Scene(EntityManager entityManager,
                    CollisionManager collisionManager,
                    MovementManager movementManager) {
        this.entityManager = Objects.requireNonNull(entityManager, "EntityManager cannot be null");
        this.collisionManager = Objects.requireNonNull(collisionManager, "CollisionManager cannot be null");
        this.movementManager = Objects.requireNonNull(movementManager, "MovementManager cannot be null");
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
