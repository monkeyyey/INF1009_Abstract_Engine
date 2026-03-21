package com.myGame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.myGame.engine.animation.AnimationManager;
import com.myGame.engine.audio.AudioManager;
import com.myGame.engine.collision.CollisionManager;
import com.myGame.engine.lifecycle.LifecycleManager;
import com.myGame.engine.entity.EntityManager;
import com.myGame.engine.input.InputManager;
import com.myGame.engine.movement.MovementManager;
import com.myGame.engine.scene.SceneManager;
import com.myGame.engine.scene.Scene;
import com.myGame.game.factory.MathBomberFactory;
import com.myGame.game.factory.MathBomberFactoryProducer;
import com.myGame.game.input.keyboard.PauseInputSource;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.MathBomberGameStats;
import com.myGame.game.mathbomber.configurations.enums.ControlScheme;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;
import com.myGame.game.scenes.gameplay.GameScene;
import com.myGame.game.scenes.menu.EndScene;
import com.myGame.game.scenes.menu.PauseScene;
import com.myGame.game.scenes.menu.StartMenuScene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameEngine extends ApplicationAdapter {
    private static final int GLOBAL_PAUSE_INPUT_ID = 900;

    private SpriteBatch batch;
    private ShapeRenderer shape;
    private SceneManager sceneManager;
    private InputManager inputManager;
    private AudioManager audioManager;
    private StartMenuScene startMenuScene;
    private GameScene mathBomberScene;
    private EndScene endScene;
    private PauseScene pauseScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();

        inputManager = new InputManager();
        audioManager = new AudioManager();
        sceneManager = new SceneManager();
        inputManager.addInputSource(GLOBAL_PAUSE_INPUT_ID, new PauseInputSource());

        loadSoundWithFallback("explosion", "SFX/explosion.wav", "SFX/water_droplet.wav");
        loadSoundWithFallback("beep", "SFX/beep.wav", "SFX/water_droplet.wav");
        loadSoundWithFallback("ding", "SFX/ding.wav", "SFX/water_droplet.wav");
        loadSoundWithFallback("failure", "SFX/failure.wav", "SFX/water_droplet.wav");
        audioManager.loadMusic("calm", "backgroundMusic/calm_background_music.mp3");
        audioManager.loadMusic("intense", "backgroundMusic/intense_background_music.mp3");
        audioManager.setMusicVolume(0.35f);

        pauseScene = new PauseScene(
                inputManager,
                audioManager,
                () -> sceneManager.popScene(),
                this::handlePauseQuit,
                new EntityManager());

        startMenuScene = new StartMenuScene(
                inputManager,
                audioManager,
                this::startMathBomberGame,
                () -> Gdx.app.exit(),
                new EntityManager());
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
        boolean inGame = activeScene instanceof GameScene;
        boolean pausePressed = inputManager.getState(GLOBAL_PAUSE_INPUT_ID) != null
                && inputManager.getState(GLOBAL_PAUSE_INPUT_ID).isPause();

        if (inGame && !paused && pausePressed) {
            sceneManager.pushScene(pauseScene);
            return;
        }
    }

    private void startMathBomberGame(MathBomberConfig config, ControlScheme controls, QuestionMode questionMode) {
        MathBomberFactory factory = MathBomberFactoryProducer.getFactory(config, questionMode);
        mathBomberScene = new GameScene(
                inputManager,
                audioManager,
                new EntityManager(),
                new CollisionManager(),
                new MovementManager(),
                new LifecycleManager(),
                new AnimationManager(),
                config,
                controls,
                questionMode,
                this::showWinningEndScene,
                this::showLosingEndScene,
                factory);
        sceneManager.setScene(mathBomberScene);
    }

    private void showWinningEndScene(MathBomberGameStats stats) {
        showEndScene(stats, true);
    }

    private void showLosingEndScene(MathBomberGameStats stats) {
        showEndScene(stats, false);
    }

    private void handlePauseQuit() {
        if (mathBomberScene == null) {
            Gdx.app.exit();
            return;
        }
        showEndScene(mathBomberScene.buildCurrentStats(), false);
    }

    private void showEndScene(MathBomberGameStats stats, boolean didWin) {
        endScene = new EndScene(
                inputManager,
                stats,
                didWin,
                () -> Gdx.app.exit(),
                () -> sceneManager.setScene(startMenuScene),
                new EntityManager());
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
