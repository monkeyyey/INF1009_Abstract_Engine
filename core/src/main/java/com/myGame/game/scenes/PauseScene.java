package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.scenes.Scene;

public class PauseScene extends Scene {
    private final Runnable onResume;
    private final Runnable onQuit;
    private BitmapFont font;
    private float resumeX;
    private float resumeY;
    private float quitX;
    private float quitY;
    private float buttonW;
    private float buttonH;
    private boolean ignoreFirstUpdate;

    public PauseScene(Runnable onResume, Runnable onQuit) {
        super();
        this.onResume = onResume;
        this.onQuit = onQuit;
    }

    @Override
    public void onEnter() {
        font = new BitmapFont();
        ignoreFirstUpdate = true;
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        buttonW = 220f;
        buttonH = 60f;
        resumeX = (screenW - buttonW) / 2f;
        resumeY = screenH / 2f + 40f;
        quitX = (screenW - buttonW) / 2f;
        quitY = screenH / 2f - 40f - buttonH;
    }

    @Override
    public void onExit() {
        // No-op
    }

    @Override
    public void update(float dt) {
        if (ignoreFirstUpdate) {
            ignoreFirstUpdate = false;
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            onResume.run();
            return;
        }
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (hit(x, y, resumeX, resumeY, buttonW, buttonH)) {
                onResume.run();
            } else if (hit(x, y, quitX, quitY, buttonW, buttonH)) {
                onQuit.run();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, 0.7f);
        shape.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shape.setColor(Color.DARK_GRAY);
        shape.rect(resumeX, resumeY, buttonW, buttonH);
        shape.rect(quitX, quitY, buttonW, buttonH);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "RESUME", resumeX + 60f, resumeY + 38f);
        font.draw(batch, "QUIT", quitX + 80f, quitY + 38f);
        batch.end();
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }

    private boolean hit(float x, float y, float bx, float by, float bw, float bh) {
        return x >= bx && x <= bx + bw && y >= by && y <= by + bh;
    }
}
