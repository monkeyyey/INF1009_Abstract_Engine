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
import com.myGame.game.scenes.CircleArenaScene;
import com.myGame.game.scenes.DemoScene;
import com.myGame.game.scenes.PauseScene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameEngine extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private SceneManager sceneManager;
    private InputManager inputManager;
    private boolean demoActive = true;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        inputManager = new InputManager();
        sceneManager = new SceneManager();
        sceneManager.setScene(new DemoScene(inputManager));
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
        Scene active = sceneManager.getActiveScene();
        boolean paused = active instanceof PauseScene;

        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(new PauseScene(() -> {
                Scene popped = sceneManager.popScene();
                if (popped != null) popped.dispose();
            }, () -> Gdx.app.exit()));
            return;
        }

        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            demoActive = !demoActive;
            if (demoActive) {
                sceneManager.setScene(new DemoScene(inputManager));
            } else {
                sceneManager.setScene(new CircleArenaScene(inputManager));
            }
        }
    }
}
