package com.myGame.engine.scene;

import com.myGame.engine.entity.EntityManager;

public abstract class StaticScene extends Scene {
    protected StaticScene(EntityManager entityManager) {
        super(entityManager);
    }
}
