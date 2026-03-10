package com.myGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.SceneManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.simulation.input.PauseInputSource;
import com.myGame.simulation.mathbomber.config.ControlScheme;
import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.MathBomberGameStats;
import com.myGame.simulation.mathbomber.config.QuestionMode;
import com.myGame.simulation.scenes.MathBomberScene;
import com.myGame.simulation.scenes.MathBomberStartMenuScene;
import com.myGame.simulation.scenes.MathBomberWinScene;
import com.myGame.simulation.scenes.PauseScene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameEngine extends ApplicationAdapter {
    private static final int GLOBAL_PAUSE_INPUT_ID = 900;

    private SpriteBatch batch;
    private ShapeRenderer shape;
    private SceneManager sceneManager;
    private InputManager inputManager;
    private AudioManager audioManager;
    private MathBomberStartMenuScene startMenuScene;
    private MathBomberScene mathBomberScene;
    private MathBomberWinScene endScene;
    private PauseScene pauseScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        inputManager = new InputManager();
        audioManager = new AudioManager();
        sceneManager = new SceneManager();
        inputManager.addInputSource(GLOBAL_PAUSE_INPUT_ID, new PauseInputSource());

        loadSoundWithFallback("explosion", "explosion.wav", "water_droplet.wav");
        loadSoundWithFallback("beep", "beep.wav", "water_droplet.wav");
        loadSoundWithFallback("ding", "ding.wav", "water_droplet.wav");
        loadSoundWithFallback("failure", "failure.wav", "water_droplet.wav");
        audioManager.loadMusic("calm", "calm_background_music.mp3");
        audioManager.loadMusic("intense", "intense_background_music.mp3");
        audioManager.setMusicTrackVolume("calm", 0.55f);
        audioManager.setMusicTrackVolume("intense", 0.35f);

        pauseScene = new PauseScene(
                inputManager,
                audioManager,
                () -> sceneManager.popScene(),
                this::handlePauseQuit,
                new EntityManager(),
                new CollisionManager(),
                new MovementManager());

        startMenuScene = new MathBomberStartMenuScene(
                inputManager,
                audioManager,
                this::startMathBomberGame,
                () -> Gdx.app.exit(),
                new EntityManager(),
                new CollisionManager(),
                new MovementManager());
        sceneManager.setScene(startMenuScene);
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
        boolean inGame = activeScene instanceof MathBomberScene;
        boolean pausePressed = inputManager.getState(GLOBAL_PAUSE_INPUT_ID) != null
                && inputManager.getState(GLOBAL_PAUSE_INPUT_ID).isAction1JustPressed();

        if (inGame && !paused && pausePressed) {
            sceneManager.pushScene(pauseScene);
            return;
        }
    }

    private void startMathBomberGame(MathBomberConfig config, ControlScheme controls, QuestionMode questionMode) {
        mathBomberScene = new MathBomberScene(
                inputManager,
                audioManager,
                new EntityManager(),
                new CollisionManager(),
                new MovementManager(),
                config,
                controls,
                questionMode,
                this::showWinningEndScene);
        sceneManager.setScene(mathBomberScene);
    }

    private void showWinningEndScene(MathBomberGameStats stats) {
        showEndScene(stats, true);
    }

    private void handlePauseQuit() {
        if (mathBomberScene == null) {
            Gdx.app.exit();
            return;
        }
        showEndScene(mathBomberScene.buildCurrentStats(), false);
    }

    private void showEndScene(MathBomberGameStats stats, boolean didWin) {
        endScene = new MathBomberWinScene(
                inputManager,
                stats,
                didWin,
                () -> Gdx.app.exit(),
                () -> sceneManager.setScene(startMenuScene),
                new EntityManager(),
                new CollisionManager(),
                new MovementManager());
        sceneManager.setScene(endScene);
    }

    private void loadSoundWithFallback(String key, String preferredPath, String fallbackPath) {
        if (Gdx.files.internal(preferredPath).exists()) {
            audioManager.loadSound(key, preferredPath);
            return;
        }
        audioManager.loadSound(key, fallbackPath);
    }
}
