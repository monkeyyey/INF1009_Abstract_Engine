package com.myGame.engine.SceneManagement.abstractScenes;

import com.myGame.engine.Animation.AnimationManager;
import com.myGame.engine.Collision.CollisionManager;
import com.myGame.engine.Collision.LifecycleManager;
import com.myGame.engine.EntityManagement.EntityManager;
import com.myGame.engine.Movement.MovementManager;

import java.util.Objects;

public abstract class ActionScene extends Scene {
    protected final CollisionManager collisionManager;
    protected final MovementManager movementManager;
    protected final LifecycleManager lifecycleManager;
    protected final AnimationManager animationManager;

    protected ActionScene(EntityManager entityManager,
                          CollisionManager collisionManager,
                          MovementManager movementManager,
                          LifecycleManager lifecycleManager,
                          AnimationManager animationManager) {
        super(entityManager);
        this.collisionManager = Objects.requireNonNull(collisionManager, "CollisionManager cannot be null");
        this.movementManager = Objects.requireNonNull(movementManager, "MovementManager cannot be null");
        this.lifecycleManager = Objects.requireNonNull(lifecycleManager, "LifecycleManager cannot be null");
        this.animationManager = Objects.requireNonNull(animationManager, "AnimationManager cannot be null");
    }

    @Override
    public void update(float dt) {
        movementManager.update(dt, entityManager.getEntities());
        lifecycleManager.update(dt, entityManager.getEntities());
        animationManager.update(dt, entityManager.getEntities());
        collisionManager.update(entityManager.getEntities());
        entityManager.update(dt);
    }
}
