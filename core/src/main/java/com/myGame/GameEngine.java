package com.myGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.SceneManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.scenes.DemoScene1;
import com.myGame.game.scenes.DemoScene2;
import com.myGame.game.scenes.PauseScene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameEngine extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private SceneManager sceneManager;
    private InputManager inputManager;
    private DemoScene1 demoScene1;
    private DemoScene2 demoScene2;
    private PauseScene pauseScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        inputManager = new InputManager();
        sceneManager = new SceneManager();
        demoScene1 = new DemoScene1(inputManager);
        demoScene2 = new DemoScene2(inputManager);
        pauseScene = new PauseScene(
                inputManager,
                () -> sceneManager.popScene(),
                () -> Gdx.app.exit());
        sceneManager.setScene(demoScene1);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.1f, 0.1f, 0.12f, 1f);
        inputManager.update();
        handleGlobalInput();
        sceneManager.update(dt);
        sceneManager.render(batch, shape);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        batch.dispose();
        shape.dispose();
    }

    private void handleGlobalInput() {
        Scene activeScene = sceneManager.getActiveScene();
        boolean paused = activeScene instanceof PauseScene;

        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(pauseScene);
            return;
        }

        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (activeScene == demoScene1) {
                sceneManager.setScene(demoScene2);
            } else if (activeScene == demoScene2) {
                sceneManager.setScene(demoScene1);
            }
        }
    }
}
