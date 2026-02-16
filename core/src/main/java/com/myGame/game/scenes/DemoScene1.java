package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.entities.Bucket;
import com.myGame.game.entities.Droplet;
import com.myGame.game.entities.StaticTextureEntity;
import com.myGame.game.entities.Wind;
import com.myGame.game.input.KeyboardCustomInputSource;
import com.myGame.game.input.KeyboardWASDInputSource;
import java.util.ArrayList;
import java.util.List;

public class DemoScene1 extends Scene {
    private static final String CLOUD_BACKGROUND_ASSET = "cloud_background.jpeg";
    private static final String BUCKET_ASSET = "bucket.png";
    private static final String WIND_ASSET = "wind.png";
    private static final String DROPLET_ASSET = "droplet.png";
    private static final String DROPLET_SFX = "water_droplet";

    private static final int TARGET_SCORE = 100;
    private static final int DROPLET_COUNT = 10;
    private static final float BUCKET_WIDTH = 64f;
    private static final float BUCKET_HEIGHT = 64f;
    private static final float BUCKET_START_Y = 20f;
    private static final float BUCKET_SPEED = 300f;
    private static final float WIND_WIDTH = 96f;
    private static final float WIND_HEIGHT = 48f;
    private static final float WIND_SPEED = 220f;
    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final int bucketPlayerId = 0;
    private final int windPlayerId = 1;
    private Bucket bucket;
    private Wind wind;
    private StaticTextureEntity cloudBackground;
    private final List<Droplet> droplets = new ArrayList<>();
    private static final float DROPLET_FALL_SPEED = -200f;
    private static final float WIND_PUSH_SPEED = 140f;
    private BitmapFont hudFont;
    private boolean initialized = false;
    private int score = 0;
    private float elapsedTime = 0f;
    private float completionTime = 0f;
    private boolean gameCompleted = false;
    private boolean restartLatch = false;
    private int dropletNameSeq = 0;

    public DemoScene1(InputManager inputManager, AudioManager audioManager) {
        super();
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
        hudFont = new BitmapFont();
        hudFont.setColor(Color.WHITE);
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        cloudBackground = new StaticTextureEntity(CLOUD_BACKGROUND_ASSET, 0f, 0f, screenW, screenH);
        entityManager.addEntity("cloud_background", cloudBackground);
        createCoreEntities(screenW, screenH);
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
        handleRestartInput(bucketInput, screenW, screenH);

        if (!gameCompleted) {
            elapsedTime += dt;
        }

        InputState windInput = inputManager.getState(windPlayerId);
        applyPlayerInput(bucketInput, windInput);
        beginDropletFrame();
        super.update(dt);
        finishDropletFrame(screenW, screenH);
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
            float bucketX = (screenW - bucket.getWidth()) / 2f;
            bucket.setX(bucketX);
            bucket.setY(BUCKET_START_Y);
            bucket.setVelocityX(0f);
            bucket.setVelocityY(0f);
        }
        if (wind != null) {
            float windX = (screenW - wind.getWidth()) / 2f;
            float windY = screenH / 2f;
            wind.setX(windX);
            wind.setY(windY);
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

    private String formatTime(float totalSeconds) {
        int minutes = (int) (totalSeconds / 60f);
        int seconds = (int) (totalSeconds % 60f);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void spawnDroplets(float screenW, float screenH) {
        float dropW = 64f;
        float dropH = 64f;
        float dropY = screenH - dropH - 10f;
        for (int i = 0; i < DROPLET_COUNT; i++) {
            float dropX = MathUtils.random(0f, screenW - dropW);
            Droplet droplet = new Droplet(
                    DROPLET_ASSET, dropX, dropY + i * 60f, dropW, dropH,
                    DROPLET_FALL_SPEED, WIND_PUSH_SPEED,
                    () -> audioManager.playSound(DROPLET_SFX));
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

    private void createCoreEntities(float screenW, float screenH) {
        float bucketX = (screenW - BUCKET_WIDTH) / 2f;
        bucket = new Bucket(BUCKET_ASSET, bucketX, BUCKET_START_Y, BUCKET_WIDTH, BUCKET_HEIGHT,
                BUCKET_SPEED, 0f, screenW - BUCKET_WIDTH);

        float windX = (screenW - WIND_WIDTH) / 2f;
        float windY = screenH / 2f;
        wind = new Wind(WIND_ASSET, windX, windY, WIND_WIDTH, WIND_HEIGHT,
                WIND_SPEED, 0f, screenW - WIND_WIDTH, 0f, screenH - WIND_HEIGHT);

        entityManager.addEntity("bucket", bucket);
        entityManager.addEntity("wind", wind);
    }

    private void handleRestartInput(InputState bucketInput, float screenW, float screenH) {
        if (bucketInput == null) return;
        if (bucketInput.isAction2()) {
            if (!restartLatch) {
                resetGame(screenW, screenH);
                restartLatch = true;
            }
        } else {
            restartLatch = false;
        }
    }

    private void applyPlayerInput(InputState bucketInput, InputState windInput) {
        if (bucketInput != null && bucket != null && bucket.isActive()) {
            bucket.applyInput(bucketInput);
        }
        if (windInput != null && wind != null && wind.isActive()) {
            wind.applyInput(windInput);
        }
    }

    private void beginDropletFrame() {
        for (Droplet d : droplets) {
            if (!d.isActive()) continue;
            d.beginFrame();
        }
    }

    private void finishDropletFrame(float screenW, float screenH) {
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
}
