package com.myGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.AudioManager;
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
    private AudioManager audioManager;
    private DemoScene1 demoScene1;
    private DemoScene2 demoScene2;
    private PauseScene pauseScene;

    @Override
    public void create() {

        // Renderers
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        // Instantiate managers
        inputManager = new InputManager();
        audioManager = new AudioManager();
        sceneManager = new SceneManager();

        // Load and configure Sound effects and background music
        audioManager.loadSound("water_droplet", "water_droplet.wav");
        audioManager.loadMusic("calm", "calm_background_music.mp3");
        audioManager.loadMusic("intense", "intense_background_music.mp3");
        audioManager.setMusicTrackVolume("calm", 0.55f);
        audioManager.setMusicTrackVolume("intense", 0.35f);

        // Create and configure game and pause menu scenes
        demoScene1 = new DemoScene1(inputManager, audioManager);
        demoScene2 = new DemoScene2(inputManager, audioManager);
        sceneManager.registerCycleScene(demoScene1);
        sceneManager.registerCycleScene(demoScene2);
        pauseScene = new PauseScene(
                inputManager,
                audioManager,
                () -> sceneManager.popScene(),
                () -> Gdx.app.exit());

        // Start with demoScene1
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
        audioManager.dispose();
        batch.dispose();
        shape.dispose();
    }

    private void handleGlobalInput() {
        Scene activeScene = sceneManager.getActiveScene();
        boolean paused = activeScene instanceof PauseScene;

        // switch to pause scene
        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sceneManager.pushScene(pauseScene);
            return;
        }

        // Cycle within game scenes
        if (!paused && Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            sceneManager.cycleScene();
        }
    }
}
