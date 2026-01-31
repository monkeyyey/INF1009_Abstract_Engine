package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.entities.Bucket;
import com.myGame.game.entities.Droplet;
import com.myGame.game.entities.Wind;
import com.myGame.game.input.KeyboardArrowInputSource;
import com.myGame.game.input.KeyboardWASDInputSource;
import java.util.ArrayList;
import java.util.List;

public class DemoScene1 extends Scene {
    private final InputManager inputManager;
    private final int bucketPlayerId = 0;
    private final int windPlayerId = 1;
    private Bucket bucket;
    private Wind wind;
    private final List<Droplet> droplets = new ArrayList<>();
    private static final float DROPLET_FALL_SPEED = -200f;
    private static final float WIND_PUSH_SPEED = 140f;

    public DemoScene1(InputManager inputManager) {
        super();
        this.inputManager = inputManager;
    }

    @Override
    public void onEnter() {
        inputManager.addPlayer(bucketPlayerId, new KeyboardArrowInputSource());
        inputManager.addPlayer(windPlayerId, new KeyboardWASDInputSource());
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float bucketW = 64f;
        float bucketH = 64f;
        float bucketX = (screenW - bucketW) / 2f;
        float bucketY = 20f;
        float bucketSpeed = 300f;
        bucket = new Bucket("bucket.png", bucketX, bucketY, bucketW, bucketH, bucketSpeed, 0f, screenW - bucketW);

        float windW = 96f;
        float windH = 48f;
        float windX = (screenW - windW) / 2f;
        float windY = screenH / 2f;
        float windSpeed = 220f;
        wind = new Wind("wind.png", windX, windY, windW, windH, windSpeed, 0f, screenW - windW, 0f, screenH - windH);

        float dropW = 64f;
        float dropH = 64f;
        float dropY = screenH - dropH - 10f;
        for (int i = 0; i < 10; i++) {
            float dropX = MathUtils.random(0f, screenW - dropW);
            Droplet droplet = new Droplet("droplet.png", dropX, dropY + i * 60f, dropW, dropH);
            droplet.setVelocityY(DROPLET_FALL_SPEED);
            droplets.add(droplet);
            entityManager.addEntity("droplet_" + i, droplet);
        }

        entityManager.addEntity("bucket", bucket);
        entityManager.addEntity("wind", wind);
    }

    @Override
    public void onExit() {
        // No-op
    }

    @Override
    public void update(float dt) {
        InputState bucketInput = inputManager.getState(bucketPlayerId);
        if (bucketInput != null && bucket != null && bucket.isActive()) {
            bucket.applyInput(bucketInput);
        }
        InputState windInput = inputManager.getState(windPlayerId);
        if (windInput != null && wind != null && wind.isActive()) {
            wind.applyInput(windInput);
        }
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        for (Droplet d : droplets) {
            if (!d.isActive()) continue;
            applyWindToDroplet(d);
        }
        super.update(dt);
        for (Droplet d : droplets) {
            if (!d.isActive()) continue;
            if (d.isCaught() || d.getY() + d.getHeight() < 0f) {
                respawnDroplet(d, screenW, screenH);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        batch.begin();
        entityManager.draw(batch, shape);
        batch.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
    }

    private void respawnDroplet(Droplet d, float screenW, float screenH) {
        float newX = MathUtils.random(0f, screenW - d.getWidth());
        float newY = screenH - d.getHeight() - 10f;
        d.reset(newX, newY, DROPLET_FALL_SPEED);
    }

    private void applyWindToDroplet(Droplet d) {
        if (wind == null || !wind.isActive()) {
            if (!d.isCaught()) {
                d.setVelocityX(0f);
                d.setVelocityY(DROPLET_FALL_SPEED);
            }
            return;
        }

        boolean overlaps = d.getHitbox().collidesWith(wind.getHitbox());
        if (!overlaps) {
            d.setVelocityX(0f);
            d.setVelocityY(DROPLET_FALL_SPEED);
            return;
        }

        float windTop = wind.getY() + wind.getHeight();
        boolean xOverlap = d.getX() < wind.getX() + wind.getWidth()
                && d.getX() + d.getWidth() > wind.getX();
        boolean onTop = xOverlap && Math.abs(d.getY() - windTop) <= 3f;

        if (onTop) {
            d.setVelocityX(0f);
            d.setVelocityY(0f);
            return;
        }

        float dCenterX = d.getX() + d.getWidth() / 2f;
        float wCenterX = wind.getX() + wind.getWidth() / 2f;
        d.setVelocityY(DROPLET_FALL_SPEED);
        if (dCenterX < wCenterX) {
            d.setVelocityX(-WIND_PUSH_SPEED);
        } else {
            d.setVelocityX(WIND_PUSH_SPEED);
        }
    }
}
