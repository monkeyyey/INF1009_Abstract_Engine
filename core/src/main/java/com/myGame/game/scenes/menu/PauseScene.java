package com.myGame.game.scenes.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.audio.AudioManager;
import com.myGame.engine.entity.EntityManager;
import com.myGame.engine.input.InputManager;
import com.myGame.engine.input.InputState;
import com.myGame.engine.scene.StaticScene;
import com.myGame.game.input.keyboard.PauseInputSource;
import com.myGame.game.input.mouse.MouseClickInputSource;
import com.myGame.game.ui.Button;
import com.myGame.game.ui.GameFontFactory;
import com.myGame.game.ui.VolumeSlider;

public class PauseScene extends StaticScene {
    private static final int PAUSE_INPUT_ID = 100;
    private static final int PAUSE_POINTER_INPUT_ID = 101;
    private static final float VOLUME_STEP_PER_SEC = 0.8f;

    private final InputManager pauseInputManager;
    private final AudioManager audioManager;
    private final Runnable onResume;
    private final Runnable onQuit;
    private BitmapFont font;
    private BitmapFont smallFont;
    private GlyphLayout glyphLayout;
    private float musicVolume = 0.5f;
    private float sfxVolume = 1f;
    private boolean blockEscapeUntilReleased;
    private boolean initialized;
    private Button resumeButton;
    private Button quitButton;
    private VolumeSlider musicVolumeSlider;
    private VolumeSlider sfxVolumeSlider;

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
            smallFont = GameFontFactory.regular(16);
            glyphLayout = new GlyphLayout();
            initialized = true;
        }
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float sliderW = screenW * 0.5f;
        float sliderH = Math.max(16f, screenH * 0.025f);
        float sliderX = (screenW - sliderW) / 2f;
        float musicSliderY = screenH * 0.54f;
        float sfxSliderY = musicSliderY - 62f;
        float buttonW = Math.min(240f, screenW * 0.4f);
        float buttonH = Math.max(48f, screenH * 0.08f);
        float buttonGap = buttonH * 0.33f;
        float buttonX = (screenW - buttonW) / 2f;
        float resumeY = sfxSliderY - (buttonH + buttonGap + screenH * 0.10f);
        float quitY = resumeY - (buttonH + buttonGap);

        musicVolumeSlider = new VolumeSlider(sliderX, musicSliderY, sliderW, sliderH, Color.DARK_GRAY, Color.GREEN, musicVolume);
        sfxVolumeSlider = new VolumeSlider(sliderX, sfxSliderY, sliderW, sliderH, Color.DARK_GRAY, Color.GOLD, sfxVolume);
        resumeButton = new Button("Resume [Esc]", buttonX, resumeY, buttonW, buttonH, Color.DARK_GRAY, onResume);
        quitButton = new Button("Quit", buttonX, quitY, buttonW, buttonH, Color.DARK_GRAY, onQuit);

        musicVolume = audioManager.getMusicVolume();
        sfxVolume = audioManager.getSfxVolume();
        musicVolumeSlider.setValue(musicVolume);
        sfxVolumeSlider.setValue(sfxVolume);

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

        if (input.isPause()) {
            onResume.run();
            return;
        }
        float musicDelta = 0f;
        if (input.isUp()) musicDelta += VOLUME_STEP_PER_SEC * dt;
        if (input.isDown()) musicDelta -= VOLUME_STEP_PER_SEC * dt;
        if (musicDelta != 0f) {
            musicVolume = Math.max(0f, Math.min(1f, musicVolume + musicDelta));
            musicVolumeSlider.setValue(musicVolume);
            audioManager.setMusicVolume(musicVolume);
        }

        float sfxDelta = 0f;
        if (input.isRight()) sfxDelta += VOLUME_STEP_PER_SEC * dt;
        if (input.isLeft()) sfxDelta -= VOLUME_STEP_PER_SEC * dt;
        if (sfxDelta != 0f) {
            sfxVolume = Math.max(0f, Math.min(1f, sfxVolume + sfxDelta));
            sfxVolumeSlider.setValue(sfxVolume);
            audioManager.setSfxVolume(sfxVolume);
        }

        if (!pointer.isJustTouched()) return;
        float touchX = pointer.getPointerX();
        float touchY = pointer.getPointerY();
        if (resumeButton.handleClick(touchX, touchY)) return;
        if (quitButton.handleClick(touchX, touchY)) return;
        if (musicVolumeSlider.contains(touchX, touchY)) {
            applyMusicVolumeFromPointer(touchX);
            return;
        }
        if (sfxVolumeSlider.contains(touchX, touchY)) {
            applySfxVolumeFromPointer(touchX);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float sliderX = musicVolumeSlider.getX();
        float musicSliderY = musicVolumeSlider.getY();
        float sfxSliderY = sfxVolumeSlider.getY();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, 0.7f);
        shape.rect(0f, 0f, screenW, screenH);
        musicVolumeSlider.draw(shape);
        sfxVolumeSlider.draw(shape);
        resumeButton.draw(shape);
        quitButton.draw(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        String volumeText = "Music Volume: " + Math.round(musicVolume * 100f) + "%";
        glyphLayout.setText(font, volumeText);
        float volumeTextX = sliderX + (musicVolumeSlider.getWidth() - glyphLayout.width) * 0.5f;
        font.draw(batch, glyphLayout, volumeTextX, musicSliderY + 45f);

        String sfxText = "SFX Volume: " + Math.round(sfxVolume * 100f) + "%";
        glyphLayout.setText(font, sfxText);
        float sfxTextX = sliderX + (sfxVolumeSlider.getWidth() - glyphLayout.width) * 0.5f;
        font.draw(batch, glyphLayout, sfxTextX, sfxSliderY + 45f);

        String helpText = "UP/DOWN music, LEFT/RIGHT SFX. ESC resume";
        glyphLayout.setText(smallFont, helpText);
        float helpTextX = sliderX + (musicVolumeSlider.getWidth() - glyphLayout.width) * 0.5f;
        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, glyphLayout, helpTextX, sfxSliderY - 18f);
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

    private void applyMusicVolumeFromPointer(float touchX) {
        float relative = (touchX - musicVolumeSlider.getX()) / musicVolumeSlider.getWidth();
        musicVolume = Math.max(0f, Math.min(1f, relative));
        musicVolumeSlider.setValue(musicVolume);
        audioManager.setMusicVolume(musicVolume);
    }

    private void applySfxVolumeFromPointer(float touchX) {
        float relative = (touchX - sfxVolumeSlider.getX()) / sfxVolumeSlider.getWidth();
        sfxVolume = Math.max(0f, Math.min(1f, relative));
        sfxVolumeSlider.setValue(sfxVolume);
        audioManager.setSfxVolume(sfxVolume);
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }
    }
}
