package com.myGame.simulation.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.simulation.entities.RectangleWall;
import com.myGame.simulation.entities.PlayerCircle;
import com.myGame.simulation.input.KeyboardArrowInputSource;
import com.myGame.simulation.input.MouseInputSource;
import java.util.ArrayList;
import java.util.List;

public class DemoScene2 extends Scene {
    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final int arrowPlayerId = 0;
    private final int mousePlayerId = 1;
    private PlayerCircle arrowPlayer;
    private PlayerCircle mousePlayer;
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
            final float RECT_WIDTH = 80f;
            final float RECT_HEIGHT = 40f;
            final int NUMBER_OF_RECTANGLES = 3;
            final float EDGE_PADDING = 12f;

            // Get Screen Dimensions
            float screenW = Gdx.graphics.getWidth();
            float screenH = Gdx.graphics.getHeight();
            float topY = screenH - PLAYER_RADIUS - EDGE_PADDING;

            /// Create Players
            arrowPlayer = new PlayerCircle(PLAYER_RADIUS + EDGE_PADDING, topY, PLAYER_RADIUS, PLAYER_SPEED,
                    0f, screenW, 0f, screenH, Color.GREEN);
            mousePlayer = new PlayerCircle(screenW - PLAYER_RADIUS - EDGE_PADDING, topY, PLAYER_RADIUS, PLAYER_SPEED,
                    0f, screenW, 0f, screenH, Color.YELLOW);

            // Add players to entity list
            entityManager.addEntity("arrow_player", arrowPlayer);
            entityManager.addEntity("mouse_player", mousePlayer);

            // Create rectangle walls
            float segment = screenW / (NUMBER_OF_RECTANGLES + 1f);
            float wallY = (screenH - RECT_HEIGHT) * 0.5f;
            for (int i = 0; i < NUMBER_OF_RECTANGLES; i++) {
                float x = segment * (i + 1) - RECT_WIDTH / 2f;
                RectangleWall rect = new RectangleWall(x, wallY, RECT_WIDTH, RECT_HEIGHT, Color.BLUE);
                rects.add(rect);
                entityManager.addEntity("rect_" + i, rect);
            }
        }

        inputManager.addInputSource(arrowPlayerId, new KeyboardArrowInputSource());
        inputManager.addInputSource(mousePlayerId, new MouseInputSource(mousePlayer));
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
        InputState mouseInput = inputManager.getState(mousePlayerId);
        if (mouseInput != null && mousePlayer != null && mousePlayer.isActive()) {
            mousePlayer.applyInput(mouseInput);
        }
        resetWallColors();
        resetCircleColors();
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

    private void resetCircleColors() {
        if (arrowPlayer != null && arrowPlayer.isActive()) {
            arrowPlayer.setColor(Color.GREEN);
        }
        if (mousePlayer != null && mousePlayer.isActive()) {
            mousePlayer.setColor(Color.YELLOW);
        }
    }
}
