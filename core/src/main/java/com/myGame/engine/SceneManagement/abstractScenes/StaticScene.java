package com.myGame.engine.SceneManagement.abstractScenes;

import com.myGame.engine.EntityManagement.EntityManager;

public abstract class StaticScene extends Scene {
    protected StaticScene(EntityManager entityManager) {
        super(entityManager);
    }
}
