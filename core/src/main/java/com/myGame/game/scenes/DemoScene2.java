package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.entities.RectangleWall;
import com.myGame.game.entities.PlayerCircle;
import com.myGame.game.input.KeyboardArrowInputSource;
import com.myGame.game.input.MouseInputSource;
import java.util.ArrayList;
import java.util.List;

public class DemoScene2 extends Scene {
    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final int arrowPlayerId = 0;
    private final int wasdPlayerId = 1;
    private PlayerCircle arrowPlayer;
    private PlayerCircle wasdPlayer;
    private final List<RectangleWall> rects = new ArrayList<>();
    private boolean initialized = false;

    public DemoScene2(InputManager inputManager, AudioManager audioManager) {
        super();
        this.inputManager = inputManager;
        this.audioManager = audioManager;
    }

    @Override
    public void onEnter() {
        audioManager.playMusic("intense");
        if (!initialized) {
            initialized = true;
            // Constants
            final float PLAYER_RADIUS = 20f;
            final float PLAYER_SPEED = 240f;
            final float ARROW_PLAYER_X_RATIO = 0.3f;
            final float WASD_PLAYER_X_RATIO = 0.7f;
            final float PLAYER_Y_RATIO = 0.5f;
            final float RECT_WIDTH = 80f;
            final float RECT_HEIGHT = 40f;
            final int NUMBER_OF_RECTANGLES = 3;

            // Get Screen Dimensions
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();

            /// Create Players
            arrowPlayer = new PlayerCircle(screenW * ARROW_PLAYER_X_RATIO, screenH * PLAYER_Y_RATIO, PLAYER_RADIUS, PLAYER_SPEED,
                    0f, screenW, 0f, screenH, Color.GREEN);
            wasdPlayer = new PlayerCircle(screenW * WASD_PLAYER_X_RATIO, screenH * PLAYER_Y_RATIO, PLAYER_RADIUS, PLAYER_SPEED,
                    0f, screenW, 0f, screenH, Color.YELLOW);

            // Add players to entity list
            entityManager.addEntity("arrow_player", arrowPlayer);
            entityManager.addEntity("wasd_player", wasdPlayer);

            // Create rectangle walls
            for (int i = 0; i < NUMBER_OF_RECTANGLES; i++) {
                float x = MathUtils.random(0f, screenW - RECT_WIDTH);
                float y = MathUtils.random(0f, screenH - RECT_HEIGHT);
                RectangleWall rect = new RectangleWall(x, y, RECT_WIDTH, RECT_HEIGHT, Color.BLUE);
                rects.add(rect);
                entityManager.addEntity("rect_" + i, rect);
            }
        }

        inputManager.addInputSource(arrowPlayerId, new KeyboardArrowInputSource());
        inputManager.addInputSource(wasdPlayerId, new MouseInputSource(wasdPlayer));
    }

    @Override
    public void onExit() {
        // No-op
    }

    @Override
    public void update(float dt) {
        InputState arrowInput = inputManager.getState(arrowPlayerId);
        if (arrowInput != null && arrowPlayer != null && arrowPlayer.isActive()) {
            arrowPlayer.applyInput(arrowInput);
        }
        InputState wasdInput = inputManager.getState(wasdPlayerId);
        if (wasdInput != null && wasdPlayer != null && wasdPlayer.isActive()) {
            wasdPlayer.applyInput(wasdInput);
        }
        resetWallColors();
        super.update(dt);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.drawShapes(shape);
        shape.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
    }

    private void resetWallColors() {
        for (RectangleWall rect : rects) {
            rect.setColor(Color.BLUE);
        }
    }
}
