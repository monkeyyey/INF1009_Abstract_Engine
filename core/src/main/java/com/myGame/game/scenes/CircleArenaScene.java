package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.entities.RectangleWall;
import com.myGame.game.entities.PlayerCircle;
import com.myGame.game.input.KeyboardArrowInputSource;
import com.myGame.game.input.KeyboardWASDInputSource;
import java.util.ArrayList;
import java.util.List;

public class CircleArenaScene extends Scene {
    private final InputManager inputManager;
    private final int arrowPlayerId = 0;
    private final int wasdPlayerId = 1;
    private PlayerCircle arrowPlayer;
    private PlayerCircle wasdPlayer;
    private final List<RectangleWall> rects = new ArrayList<>();

    public CircleArenaScene(InputManager inputManager) {
        super();
        this.inputManager = inputManager;
    }

    @Override
    public void onEnter() {
        inputManager.addPlayer(arrowPlayerId, new KeyboardArrowInputSource());
        inputManager.addPlayer(wasdPlayerId, new KeyboardWASDInputSource());
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float radius = 20f;
        float speed = 240f;
        arrowPlayer = new PlayerCircle(screenW * 0.3f, screenH * 0.5f, radius, speed,
                0f, screenW, 0f, screenH, Color.GREEN);
        wasdPlayer = new PlayerCircle(screenW * 0.7f, screenH * 0.5f, radius, speed,
                0f, screenW, 0f, screenH, Color.YELLOW);

        entityManager.addEntity("arrow_player", arrowPlayer);
        entityManager.addEntity("wasd_player", wasdPlayer);

        float rectW = 80f;
        float rectH = 40f;
        for (int i = 0; i < 3; i++) {
            float x = MathUtils.random(0f, screenW - rectW);
            float y = MathUtils.random(0f, screenH - rectH);
            RectangleWall rect = new RectangleWall(x, y, rectW, rectH, Color.BLUE);
            rects.add(rect);
            entityManager.addEntity("rect_" + i, rect);
        }
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
        super.update(dt);
        resolveOverlaps(arrowPlayer);
        resolveOverlaps(wasdPlayer);
        updateRectColors();
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.draw(batch, shape);
        shape.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
    }

    private void updateRectColors() {
        for (RectangleWall rect : rects) {
            boolean touching = circleRectOverlap(arrowPlayer, rect) || circleRectOverlap(wasdPlayer, rect);
            rect.setColor(touching ? Color.RED : Color.BLUE);
        }
    }

    private void resolveOverlaps(PlayerCircle player) {
        if (player == null || !player.isActive()) return;
        for (RectangleWall rect : rects) {
            if (circleRectOverlap(player, rect)) {
                pushCircleOutOfRect(player, rect);
            }
        }
    }

    private boolean circleRectOverlap(PlayerCircle circle, RectangleWall rect) {
        if (circle == null || rect == null) return false;
        float cx = circle.getX();
        float cy = circle.getY();
        float rx = rect.getX();
        float ry = rect.getY();
        float rw = rect.getWidth();
        float rh = rect.getHeight();
        float closestX = clamp(cx, rx, rx + rw);
        float closestY = clamp(cy, ry, ry + rh);
        float dx = cx - closestX;
        float dy = cy - closestY;
        return (dx * dx + dy * dy) <= (circle.getRadius() * circle.getRadius());
    }

    private void pushCircleOutOfRect(PlayerCircle circle, RectangleWall rect) {
        float cx = circle.getX();
        float cy = circle.getY();
        float rx = rect.getX();
        float ry = rect.getY();
        float rw = rect.getWidth();
        float rh = rect.getHeight();
        float r = circle.getRadius();

        float closestX = clamp(cx, rx, rx + rw);
        float closestY = clamp(cy, ry, ry + rh);
        float dx = cx - closestX;
        float dy = cy - closestY;

        if (dx == 0f && dy == 0f) {
            float left = cx - rx;
            float right = (rx + rw) - cx;
            float bottom = cy - ry;
            float top = (ry + rh) - cy;
            float min = Math.min(Math.min(left, right), Math.min(bottom, top));
            if (min == left) cx = rx - r;
            else if (min == right) cx = rx + rw + r;
            else if (min == bottom) cy = ry - r;
            else cy = ry + rh + r;
        } else {
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            float overlap = r - dist;
            if (overlap > 0f && dist != 0f) {
                cx += (dx / dist) * overlap;
                cy += (dy / dist) * overlap;
            }
        }

        circle.setX(cx);
        circle.setY(cy);
        circle.getHitbox().setX(cx);
        circle.getHitbox().setY(cy);
    }

    private float clamp(float v, float min, float max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
