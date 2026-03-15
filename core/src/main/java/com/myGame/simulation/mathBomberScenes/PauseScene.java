package com.myGame.simulation.mathBomberScenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.AudioManagement.AudioManager;
import com.myGame.engine.EntityManagement.EntityManager;
import com.myGame.engine.InputManagement.InputManager;
import com.myGame.engine.InputManagement.Interfaces.InputState;
import com.myGame.engine.SceneManagement.abstractScenes.StaticScene;
import com.myGame.simulation.input.MouseClickInputSource;
import com.myGame.simulation.input.PauseInputSource;
import com.myGame.simulation.ui.Button;
import com.myGame.simulation.ui.GameFontFactory;
import com.myGame.simulation.ui.VolumeSlider;

public class PauseScene extends StaticScene {
    private static final int PAUSE_INPUT_ID = 100;
    private static final int PAUSE_POINTER_INPUT_ID = 101;
    private static final float VOLUME_STEP_PER_SEC = 0.8f;

    private final InputManager pauseInputManager;
    private final AudioManager audioManager;
    private final Runnable onResume;
    private final Runnable onQuit;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private float musicVolume = 0.5f;
    private boolean blockEscapeUntilReleased;
    private boolean initialized;
    private Button resumeButton;
    private Button quitButton;
    private VolumeSlider volumeSlider;

    public PauseScene(InputManager pauseInputManager,
                      AudioManager audioManager,
                      Runnable onResume,
                      Runnable onQuit,
                      EntityManager entityManager) {
        super(entityManager);
        this.pauseInputManager = pauseInputManager;
        this.audioManager = audioManager;
        this.onResume = onResume;
        this.onQuit = onQuit;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            font = GameFontFactory.regular(24);
            glyphLayout = new GlyphLayout();
            initialized = true;
        }
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float sliderW = screenW * 0.5f;
        float sliderH = Math.max(16f, screenH * 0.025f);
        float sliderX = (screenW - sliderW) / 2f;
        float sliderY = screenH * 0.5f;
        float buttonW = Math.min(240f, screenW * 0.4f);
        float buttonH = Math.max(48f, screenH * 0.08f);
        float buttonGap = buttonH * 0.33f;
        float buttonX = (screenW - buttonW) / 2f;
        float resumeY = sliderY - (buttonH + buttonGap + screenH * 0.10f);
        float quitY = resumeY - (buttonH + buttonGap);

        volumeSlider = new VolumeSlider(sliderX, sliderY, sliderW, sliderH, Color.DARK_GRAY, Color.GREEN, musicVolume);
        resumeButton = new Button("RESUME", buttonX, resumeY, buttonW, buttonH, Color.DARK_GRAY, onResume);
        quitButton = new Button("QUIT", buttonX, quitY, buttonW, buttonH, Color.DARK_GRAY, onQuit);

        float currentMusicVolume = audioManager.getMusicVolume();
        if (currentMusicVolume > 0f) {
            musicVolume = currentMusicVolume;
            volumeSlider.setValue(musicVolume);
        }

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

        if (input.isAction1JustPressed()) {
            onResume.run();
            return;
        }
        if (input.isAction2JustPressed()) {
            onQuit.run();
            return;
        }

        float delta = 0f;
        if (input.isUp() || input.isRight()) delta += VOLUME_STEP_PER_SEC * dt;
        if (input.isDown() || input.isLeft()) delta -= VOLUME_STEP_PER_SEC * dt;
        if (delta != 0f) {
            musicVolume = Math.max(0f, Math.min(1f, musicVolume + delta));
            volumeSlider.setValue(musicVolume);
            audioManager.setMusicVolume(musicVolume);
        }

        if (!pointer.isJustTouched()) return;
        float touchX = pointer.getPointerX();
        float touchY = pointer.getPointerY();
        if (resumeButton.handleClick(touchX, touchY)) return;
        if (quitButton.handleClick(touchX, touchY)) return;
        if (volumeSlider.contains(touchX, touchY)) {
            applyVolumeFromPointer(touchX);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float sliderX = volumeSlider.getX();
        float sliderY = volumeSlider.getY();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, 0.7f);
        shape.rect(0f, 0f, screenW, screenH);
        volumeSlider.draw(shape);
        resumeButton.draw(shape);
        quitButton.draw(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        String volumeText = "Volume: " + Math.round(musicVolume * 100f) + "%";
        glyphLayout.setText(font, volumeText);
        float volumeTextX = sliderX + (volumeSlider.getWidth() - glyphLayout.width) * 0.5f;
        font.draw(batch, glyphLayout, volumeTextX, sliderY + 45f);

        String helpText = "UP/RIGHT louder, DOWN/LEFT quieter. ESC resume, Q quit";
        glyphLayout.setText(font, helpText);
        float helpTextX = sliderX + (volumeSlider.getWidth() - glyphLayout.width) * 0.5f;
        font.draw(batch, glyphLayout, helpTextX, sliderY - 18f);
        drawCenteredButtonLabel(batch, resumeButton);
        drawCenteredButtonLabel(batch, quitButton);
        batch.end();
    }

    private void drawCenteredButtonLabel(SpriteBatch batch, Button button) {
        glyphLayout.setText(font, button.getLabel());
        float textX = button.getX() + (button.getWidth() - glyphLayout.width) / 2f;
        float textY = button.getY() + (button.getHeight() + glyphLayout.height) / 2f;
        font.draw(batch, glyphLayout, textX, textY);
    }

    private void applyVolumeFromPointer(float touchX) {
        float relative = (touchX - volumeSlider.getX()) / volumeSlider.getWidth();
        musicVolume = Math.max(0f, Math.min(1f, relative));
        volumeSlider.setValue(musicVolume);
        audioManager.setMusicVolume(musicVolume);
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
