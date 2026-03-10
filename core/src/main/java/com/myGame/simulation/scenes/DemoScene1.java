package com.myGame.simulation.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.simulation.entities.Bucket;
import com.myGame.simulation.entities.Droplet;
import com.myGame.simulation.entities.StaticTextureEntity;
import com.myGame.simulation.entities.Wind;
import com.myGame.simulation.input.KeyboardCustomInputSource;
import com.myGame.simulation.input.KeyboardWASDInputSource;
import com.myGame.simulation.ui.GameFontFactory;

import java.util.ArrayList;
import java.util.List;

public class DemoScene1 extends Scene {
    private static final int TARGET_SCORE = 100;
    private static final int DROPLET_COUNT = 10;
    private static final float DROPLET_FALL_SPEED = -200f;

    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final int bucketPlayerId = 0;
    private final int windPlayerId = 1;

    private Bucket bucket;
    private Wind wind;
    private StaticTextureEntity cloudBackground;
    private final List<Droplet> droplets = new ArrayList<>();
    private BitmapFont hudFont;

    private boolean initialized;
    private boolean gameCompleted;
    private boolean restartLatch;
    private int score;
    private int dropletNameSeq;
    private float elapsedTime;
    private float completionTime;

    public DemoScene1(InputManager inputManager,
                      AudioManager audioManager,
                      EntityManager entityManager,
                      CollisionManager collisionManager,
                      MovementManager movementManager) {
        super(entityManager, collisionManager, movementManager);
        this.inputManager = inputManager;
        this.audioManager = audioManager;
    }

    @Override
    public void onEnter() {
        audioManager.playMusic("calm");
        inputManager.addInputSource(
                bucketPlayerId,
                new KeyboardCustomInputSource(
                        Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.UP, Input.Keys.DOWN,
                        Input.Keys.SPACE, Input.Keys.R, -1));
        inputManager.addInputSource(windPlayerId, new KeyboardWASDInputSource());

        if (initialized) return;
        initialized = true;

        hudFont = GameFontFactory.regular(24);
        hudFont.setColor(Color.WHITE);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        cloudBackground = new StaticTextureEntity("cloud_background.jpeg", 0f, 0f, screenW, screenH);
        entityManager.addEntity("cloud_background", cloudBackground);

        bucket = new Bucket("bucket.png", (screenW - 64f) / 2f, 20f, 64f, 64f, 300f, 0f, screenW - 64f);
        wind = new Wind("wind.png", (screenW - 96f) / 2f, screenH / 2f, 96f, 48f, 220f,
                0f, screenW - 96f, 0f, screenH - 48f);

        entityManager.addEntity("bucket", bucket);
        entityManager.addEntity("wind", wind);
        spawnDroplets(screenW, screenH);
    }

    @Override
    public void onExit() {
        // No-op
    }

    @Override
    public void update(float dt) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        InputState bucketInput = inputManager.getState(bucketPlayerId);
        if (bucketInput != null && bucketInput.isAction2() && !restartLatch) {
            resetGame(screenW, screenH);
        }
        restartLatch = bucketInput != null && bucketInput.isAction2();

        if (!gameCompleted) {
            elapsedTime += dt;
        }

        if (bucketInput != null && bucket != null && bucket.isActive()) {
            bucket.applyInput(bucketInput);
        }

        InputState windInput = inputManager.getState(windPlayerId);
        if (windInput != null && wind != null && wind.isActive()) {
            wind.applyInput(windInput);
        }

        for (Droplet d : droplets) {
            if (d.isActive()) d.beginFrame();
        }

        super.update(dt);

        boolean reachedTargetThisFrame = false;
        for (Droplet d : droplets) {
            if (!d.isActive()) continue;
            d.endFrame();

            if (d.isCaught()) {
                score++;
                if (score >= TARGET_SCORE && !gameCompleted) {
                    gameCompleted = true;
                    completionTime = elapsedTime;
                    reachedTargetThisFrame = true;
                    break;
                }
                if (!gameCompleted) {
                    respawnDroplet(d, screenW, screenH);
                }
            } else if (d.getY() + d.getHeight() < 0f) {
                respawnDroplet(d, screenW, screenH);
            }
        }

        if (reachedTargetThisFrame) {
            clearDroplets();
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        batch.begin();
        entityManager.drawSprites(batch);

        hudFont.draw(batch, "Score: " + score + " / " + TARGET_SCORE, 16f, Gdx.graphics.getHeight() - 16f);
        float shownTime = gameCompleted ? completionTime : elapsedTime;
        hudFont.draw(batch, "Time: " + formatTime(shownTime), 16f, Gdx.graphics.getHeight() - 44f);
        if (gameCompleted) {
            hudFont.draw(batch, "Completed in " + formatTime(completionTime) + "! Press R to restart.",
                    16f, Gdx.graphics.getHeight() - 72f);
        } else {
            hudFont.draw(batch, "Press R to restart", 16f, Gdx.graphics.getHeight() - 72f);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
        if (hudFont != null) {
            hudFont.dispose();
        }
    }

    private void respawnDroplet(Droplet d, float screenW, float screenH) {
        float newX = MathUtils.random(0f, screenW - d.getWidth());
        float newY = screenH - d.getHeight() - 10f;
        d.reset(newX, newY, DROPLET_FALL_SPEED);
    }

    private void resetGame(float screenW, float screenH) {
        score = 0;
        elapsedTime = 0f;
        completionTime = 0f;
        gameCompleted = false;

        if (bucket != null) {
            bucket.setX((screenW - bucket.getWidth()) / 2f);
            bucket.setY(20f);
            bucket.setVelocityX(0f);
            bucket.setVelocityY(0f);
        }

        if (wind != null) {
            wind.setX((screenW - wind.getWidth()) / 2f);
            wind.setY(screenH / 2f);
            wind.setVelocityX(0f);
            wind.setVelocityY(0f);
        }

        if (droplets.isEmpty()) {
            spawnDroplets(screenW, screenH);
            return;
        }

        float dropY = screenH - 64f - 10f;
        for (int i = 0; i < droplets.size(); i++) {
            Droplet d = droplets.get(i);
            float dropX = MathUtils.random(0f, screenW - d.getWidth());
            d.reset(dropX, dropY + i * 60f, DROPLET_FALL_SPEED);
        }
    }

    private void spawnDroplets(float screenW, float screenH) {
        float dropY = screenH - 64f - 10f;
        for (int i = 0; i < DROPLET_COUNT; i++) {
            float dropX = MathUtils.random(0f, screenW - 64f);
            Droplet droplet = new Droplet(
                    "droplet.png", dropX, dropY + i * 60f, 64f, 64f,
                    DROPLET_FALL_SPEED,
                    () -> audioManager.playSound("water_droplet"));
            droplet.setVelocityY(DROPLET_FALL_SPEED);
            droplets.add(droplet);
            entityManager.addEntity("droplet_" + (dropletNameSeq++), droplet);
        }
    }

    private void clearDroplets() {
        for (Droplet d : droplets) {
            d.destroy();
        }
        droplets.clear();
    }

    private String formatTime(float totalSeconds) {
        int minutes = (int) (totalSeconds / 60f);
        int seconds = (int) (totalSeconds % 60f);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
