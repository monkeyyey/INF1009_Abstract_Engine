package com.myGame.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.game.input.MouseClickInputSource;
import com.myGame.game.input.PauseInputSource;
import com.myGame.game.ui.Button;
import com.myGame.game.ui.VolumeSlider;

public class PauseScene extends Scene {
    private static final int PAUSE_INPUT_ID = 100;
    private static final int PAUSE_POINTER_INPUT_ID = 101;
    private static final float VOLUME_STEP_PER_SEC = 0.8f;
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 60f;
    private static final float BUTTON_GAP = 20f;
    private static final float LABEL_Y_OFFSET = 38f;
    private static final float RESUME_LABEL_X_OFFSET = 60f;
    private static final float QUIT_LABEL_X_OFFSET = 80f;

    private final InputManager pauseInputManager;
    private final Runnable onResume;
    private final Runnable onQuit;
    private BitmapFont font;
    private float musicVolume = 0.5f;
    private boolean blockEscapeUntilReleased;
    private boolean initialized;
    private Button resumeButton;
    private Button quitButton;
    private VolumeSlider volumeSlider;

    public PauseScene(InputManager pauseInputManager, Runnable onResume, Runnable onQuit) {
        super();
        this.pauseInputManager = pauseInputManager;
        this.onResume = onResume;
        this.onQuit = onQuit;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            font = new BitmapFont();
            initialized = true;
        }
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float sliderW = screenW * 0.5f;
        float sliderH = 20f;
        float sliderX = (screenW - sliderW) / 2f;
        float sliderY = screenH * 0.5f;
        float buttonX = (screenW - BUTTON_WIDTH) / 2f;
        float resumeY = screenH * 0.5f - 120f;
        float quitY = resumeY - BUTTON_HEIGHT - BUTTON_GAP;
        
        volumeSlider = new VolumeSlider(sliderX, sliderY, sliderW, sliderH, Color.DARK_GRAY, Color.GREEN, musicVolume);
        resumeButton = new Button("RESUME", buttonX, resumeY, BUTTON_WIDTH, BUTTON_HEIGHT, Color.DARK_GRAY, onResume);
        quitButton = new Button("QUIT", buttonX, quitY, BUTTON_WIDTH, BUTTON_HEIGHT, Color.DARK_GRAY, onQuit);

        pauseInputManager.addInputSource(PAUSE_INPUT_ID, new PauseInputSource());
        pauseInputManager.addInputSource(PAUSE_POINTER_INPUT_ID, new MouseClickInputSource());
        blockEscapeUntilReleased = true;
    }

    @Override
    public void onExit() {
        pauseInputManager.removeInputSource(PAUSE_INPUT_ID);
        pauseInputManager.removeInputSource(PAUSE_POINTER_INPUT_ID);
    }

    @Override
    public void update(float dt) {
        InputState input = pauseInputManager.getState(PAUSE_INPUT_ID);
        InputState pointer = pauseInputManager.getState(PAUSE_POINTER_INPUT_ID);
        if (input == null || pointer == null) return;

        if (blockEscapeUntilReleased) {
            if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                blockEscapeUntilReleased = false;
            }
            return;
        }

        if (input.isAction1()) {
            onResume.run();
            return;
        }

        float delta = 0f;
        if (input.isUp() || input.isRight()) delta += VOLUME_STEP_PER_SEC * dt;
        if (input.isDown() || input.isLeft()) delta -= VOLUME_STEP_PER_SEC * dt;
        if (delta != 0f) {
            musicVolume = Math.max(0f, Math.min(1f, musicVolume + delta));
            volumeSlider.setValue(musicVolume);
        }

        if (!pointer.isJustTouched()) return;
        float touchX = pointer.getPointerX();
        float touchY = pointer.getPointerY();
        if (resumeButton.handleClick(touchX, touchY)) return;
        quitButton.handleClick(touchX, touchY);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float sliderX = volumeSlider.getX();
        float sliderY = volumeSlider.getY();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, 0.7f);
        shape.rect(0f, 0f, screenW, Gdx.graphics.getHeight());
        volumeSlider.draw(shape);
        resumeButton.draw(shape);
        quitButton.draw(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Volume: " + Math.round(musicVolume * 100f) + "%", sliderX, sliderY + 45f);
        font.draw(batch, "UP/RIGHT louder, DOWN/LEFT quieter. ESC to resume", sliderX, sliderY - 18f);
        font.draw(batch, resumeButton.getLabel(), resumeButton.getX() + RESUME_LABEL_X_OFFSET, resumeButton.getY() + LABEL_Y_OFFSET);
        font.draw(batch, quitButton.getLabel(), quitButton.getX() + QUIT_LABEL_X_OFFSET, quitButton.getY() + LABEL_Y_OFFSET);
        batch.end();
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
