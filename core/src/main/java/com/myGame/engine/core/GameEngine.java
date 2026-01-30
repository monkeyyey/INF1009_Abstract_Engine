package com.myGame.engine.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.managers.SceneManager;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.OutputManager;

public class GameEngine extends ApplicationAdapter {
    private SceneManager sceneManager;
    private InputManager inputManager;
    private OutputManager outputManager;
    private SpriteBatch batch;
    private ShapeRenderer shape;

    @Override
    public void create() {
        // Placeholder implementation
        sceneManager = new SceneManager();
        inputManager = new InputManager();
        outputManager = new OutputManager();
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
    }

    @Override
    public void render() {
        // Placeholder loop
        inputManager.update();
        sceneManager.update(0.016f);
        sceneManager.render(batch, shape);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        sceneManager.dispose();
        outputManager.dispose();
    }
}
