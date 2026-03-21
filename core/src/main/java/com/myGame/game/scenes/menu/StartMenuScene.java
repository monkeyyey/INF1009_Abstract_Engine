package com.myGame.game.scenes.menu;

import com.badlogic.gdx.Gdx;
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
import com.myGame.game.input.keyboard.KeyboardCustomInputSource;
import com.myGame.game.input.mouse.MouseClickInputSource;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.enums.ControlScheme;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;
import com.myGame.game.ui.Button;
import com.myGame.game.ui.GameFontFactory;
import com.myGame.game.ui.VolumeSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class StartMenuScene extends StaticScene {
    private static final int MENU_POINTER_INPUT_ID = 201;
    private static final int MENU_KEYBOARD_INPUT_ID = 202;
    private static final Color TOGGLE_SELECTED = Color.ROYAL;
    private static final Color TOGGLE_NORMAL = Color.DARK_GRAY;

    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final StartGameHandler onStart;
    private final Runnable onQuit;

    private BitmapFont font;
    private BitmapFont actionFont;
    private BitmapFont smallFont;
    private BitmapFont titleFont;
    private GlyphLayout glyph;
    private boolean initialized;

    private Button startButton;
    private Button quitButton;
    private VolumeSlider musicVolumeSlider;
    private VolumeSlider sfxVolumeSlider;
    private final List<MenuButtonBinding> buttonBindings = new ArrayList<>();
    private float difficultyHeaderCenterX;
    private float difficultyHeaderY;
    private float controlsHeaderCenterX;
    private float controlsHeaderY;
    private float questionsHeaderCenterX;
    private float questionsHeaderY;

    private Difficulty selectedDifficulty = Difficulty.EASY;
    private ControlScheme selectedControls = ControlScheme.ARROW_KEYS;
    private QuestionMode selectedQuestionMode = QuestionMode.ADDITION_ONLY;

    public StartMenuScene(InputManager inputManager,
                                    AudioManager audioManager,
                                    StartGameHandler onStart,
                                    Runnable onQuit,
                                    EntityManager entityManager) {
        super(entityManager);
        this.inputManager = inputManager;
        this.audioManager = audioManager;
        this.onStart = onStart;
        this.onQuit = onQuit;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            font = GameFontFactory.regular(24);
            actionFont = GameFontFactory.regular(22);
            smallFont = GameFontFactory.regular(16);
            titleFont = GameFontFactory.boldWithBorder(56, 2f, new Color(0f, 0f, 0f, 0.35f));
            glyph = new GlyphLayout();
            initialized = true;
        }
        buildUi();
        inputManager.addInputSource(MENU_POINTER_INPUT_ID, new MouseClickInputSource());
        inputManager.addInputSource(MENU_KEYBOARD_INPUT_ID, new KeyboardCustomInputSource(
                com.badlogic.gdx.Input.Keys.LEFT,
                com.badlogic.gdx.Input.Keys.RIGHT,
                com.badlogic.gdx.Input.Keys.UP,
                com.badlogic.gdx.Input.Keys.DOWN,
                com.badlogic.gdx.Input.Keys.ENTER,
                -1,
                -1,
                com.badlogic.gdx.Input.Keys.Q));
        audioManager.playMusic("intense");
    }

    @Override
    public void onExit() {
        inputManager.removeInputSource(MENU_POINTER_INPUT_ID);
        inputManager.removeInputSource(MENU_KEYBOARD_INPUT_ID);
    }

    @Override
    public void update(float dt) {
        InputState pointer = inputManager.getState(MENU_POINTER_INPUT_ID);
        InputState keyboard = inputManager.getState(MENU_KEYBOARD_INPUT_ID);
        if (pointer == null || keyboard == null) return;

        if (keyboard.isUp()) {
            setMusicVolume(musicVolumeSlider.getValue() + dt * 0.6f);
        } else if (keyboard.isDown()) {
            setMusicVolume(musicVolumeSlider.getValue() - dt * 0.6f);
        }

        if (keyboard.isLeft()) {
            setSfxVolume(sfxVolumeSlider.getValue() - dt * 0.6f);
        } else if (keyboard.isRight()) {
            setSfxVolume(sfxVolumeSlider.getValue() + dt * 0.6f);
        }

        if (keyboard.isAction1JustPressed()) {
            startGame();
            return;
        }
        if (keyboard.isQuit()) {
            onQuit.run();
            return;
        }

        if (!pointer.isJustTouched()) return;

        float x = pointer.getPointerX();
        float y = pointer.getPointerY();
        if (handleRegisteredButtonClick(x, y)) return;
        if (musicVolumeSlider.contains(x, y)) {
            float relative = (x - musicVolumeSlider.getX()) / musicVolumeSlider.getWidth();
            setMusicVolume(relative);
            return;
        }
        if (sfxVolumeSlider.contains(x, y)) {
            float relative = (x - sfxVolumeSlider.getX()) / sfxVolumeSlider.getWidth();
            setSfxVolume(relative);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        refreshRegisteredButtonColors();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0.08f, 0.08f, 0.14f, 1f);
        shape.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (MenuButtonBinding binding : buttonBindings) {
            binding.button.draw(shape);
        }
        musicVolumeSlider.draw(shape);
        sfxVolumeSlider.draw(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        String title = "Math Bomber";
        glyph.setText(titleFont, title);
        float titleX = (Gdx.graphics.getWidth() - glyph.width) * 0.5f;
        float titleY = Gdx.graphics.getHeight() - 79f;
        titleFont.setColor(0f, 0f, 0f, 0.45f);
        titleFont.draw(batch, glyph, titleX + 2f, titleY - 2f);
        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, glyph, titleX, titleY);
        String difficultyHeader = "Choose Difficulty";
        glyph.setText(font, difficultyHeader);
        font.draw(batch, glyph, difficultyHeaderCenterX - glyph.width * 0.5f, difficultyHeaderY);

        String controlsHeader = "Choose Controls";
        glyph.setText(font, controlsHeader);
        font.draw(batch, glyph, controlsHeaderCenterX - glyph.width * 0.5f, controlsHeaderY);

        String questionsHeader = "Choose Game Mode";
        glyph.setText(font, questionsHeader);
        font.draw(batch, glyph, questionsHeaderCenterX - glyph.width * 0.5f, questionsHeaderY);

        String volumeText = "Music Volume: " + Math.round(musicVolumeSlider.getValue() * 100f) + "%";
        glyph.setText(font, volumeText);
        float volumeX = musicVolumeSlider.getX() + (musicVolumeSlider.getWidth() - glyph.width) * 0.5f;
        font.draw(batch, glyph, volumeX, musicVolumeSlider.getY() + 40f);

        String sfxText = "SFX Volume: " + Math.round(sfxVolumeSlider.getValue() * 100f) + "%";
        glyph.setText(font, sfxText);
        float sfxX = sfxVolumeSlider.getX() + (sfxVolumeSlider.getWidth() - glyph.width) * 0.5f;
        font.draw(batch, glyph, sfxX, sfxVolumeSlider.getY() + 40f);

        String volumeHelp = "UP/DOWN music, LEFT/RIGHT SFX";
        glyph.setText(smallFont, volumeHelp);
        float helpX = musicVolumeSlider.getX() + (musicVolumeSlider.getWidth() - glyph.width) * 0.5f;
        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, glyph, helpX, sfxVolumeSlider.getY() - 9f);
        for (MenuButtonBinding binding : buttonBindings) {
            drawCenteredLabel(batch, binding.button);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (actionFont != null) {
            actionFont.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
    }

    private void buildUi() {
        buttonBindings.clear();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float buttonW = Math.min(220f, w * 0.25f);
        float buttonH = Math.max(48f, h * 0.08f);
        float gap = 18f;

        float row1Y = h * 0.62f + 40f;
        float row2Y = h * 0.50f + 20f;
        float row3Y = h * 0.38f;
        float startY = h * 0.16f - 50f;
        float leftX = (w - (buttonW * 3f + gap * 2f)) / 2f;

        registerToggleButton(
                "Easy", leftX, row1Y, buttonW, buttonH,
                () -> selectedDifficulty = Difficulty.EASY,
                () -> selectedDifficulty == Difficulty.EASY);
        registerToggleButton(
                "Medium", leftX + buttonW + gap, row1Y, buttonW, buttonH,
                () -> selectedDifficulty = Difficulty.MEDIUM,
                () -> selectedDifficulty == Difficulty.MEDIUM);
        registerToggleButton(
                "Hard", leftX + (buttonW + gap) * 2f, row1Y, buttonW, buttonH,
                () -> selectedDifficulty = Difficulty.HARD,
                () -> selectedDifficulty == Difficulty.HARD);

        float controlW = buttonW;
        float controlGap = gap;
        float controlStartX = leftX;
        registerToggleButton(
                "Arrow Keys", controlStartX, row2Y, controlW, buttonH,
                () -> selectedControls = ControlScheme.ARROW_KEYS,
                () -> selectedControls == ControlScheme.ARROW_KEYS);
        registerToggleButton(
                "WASD", controlStartX + controlW + controlGap, row2Y, controlW, buttonH,
                () -> selectedControls = ControlScheme.WASD,
                () -> selectedControls == ControlScheme.WASD);
        registerToggleButton(
                "Mouse", controlStartX + (controlW + controlGap) * 2f, row2Y, controlW, buttonH,
                () -> selectedControls = ControlScheme.MOUSE,
                () -> selectedControls == ControlScheme.MOUSE);

        registerToggleButton(
                "Addition", leftX, row3Y, buttonW, buttonH,
                () -> selectedQuestionMode = QuestionMode.ADDITION_ONLY,
                () -> selectedQuestionMode == QuestionMode.ADDITION_ONLY);
        registerToggleButton(
                "Multiplication", leftX + buttonW + gap, row3Y, buttonW, buttonH,
                () -> selectedQuestionMode = QuestionMode.MULTIPLICATION_ONLY,
                () -> selectedQuestionMode == QuestionMode.MULTIPLICATION_ONLY);
        registerToggleButton(
                "Both", leftX + (buttonW + gap) * 2f, row3Y, buttonW, buttonH,
                () -> selectedQuestionMode = QuestionMode.BOTH,
                () -> selectedQuestionMode == QuestionMode.BOTH);

        float startW = Math.min(220f, w * 0.26f);
        startButton = registerActionButton(
                "Start [Enter]", (w * 0.5f) - startW - 16f, startY, startW, buttonH,
                Color.FOREST, this::startGame);
        quitButton = registerActionButton(
                "Quit [Q]", (w * 0.5f) + 16f, startY, startW, buttonH,
                Color.FIREBRICK, onQuit);

        float sliderW = Math.min(420f, w * 0.5f);
        float sliderX = (w - sliderW) / 2f;
        float musicSliderY = h * 0.27f + 22f;
        float sfxSliderY = musicSliderY - 58f;
        musicVolumeSlider = new VolumeSlider(sliderX, musicSliderY, sliderW, 18f, Color.DARK_GRAY, Color.SKY, currentMenuMusicVolume());
        sfxVolumeSlider = new VolumeSlider(sliderX, sfxSliderY, sliderW, 18f, Color.DARK_GRAY, Color.GOLD, currentMenuSfxVolume());
        difficultyHeaderCenterX = leftX + (buttonW * 3f + gap * 2f) * 0.5f;
        difficultyHeaderY = row1Y + buttonH + 28f;
        controlsHeaderCenterX = controlStartX + (controlW * 3f + controlGap * 2f) * 0.5f;
        controlsHeaderY = row2Y + buttonH + 28f;
        questionsHeaderCenterX = difficultyHeaderCenterX;
        questionsHeaderY = row3Y + buttonH + 28f;
    }

    private void refreshRegisteredButtonColors() {
        for (MenuButtonBinding binding : buttonBindings) {
            if (binding.isSelected == null) continue;
            binding.button.setColor(binding.isSelected.getAsBoolean() ? TOGGLE_SELECTED : TOGGLE_NORMAL);
        }
    }

    private void drawCenteredLabel(SpriteBatch batch, Button button) {
        BitmapFont labelFont = button == startButton || button == quitButton ? actionFont : font;
        glyph.setText(labelFont, button.getLabel());
        float tx = button.getX() + (button.getWidth() - glyph.width) * 0.5f;
        float ty = button.getY() + (button.getHeight() + glyph.height) * 0.5f;
        labelFont.setColor(Color.WHITE);
        labelFont.draw(batch, glyph, tx, ty);
    }

    private void setMusicVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        musicVolumeSlider.setValue(clamped);
        audioManager.setMusicVolume(clamped);
    }

    private void setSfxVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        sfxVolumeSlider.setValue(clamped);
        audioManager.setSfxVolume(clamped);
    }

    private float currentMenuMusicVolume() {
        float current = audioManager.getMusicVolume();
        return current > 0f ? current : 0.35f;
    }

    private float currentMenuSfxVolume() {
        float current = audioManager.getSfxVolume();
        return current > 0f ? current : 1f;
    }

    private void startGame() {
        onStart.start(configFor(selectedDifficulty), selectedControls, selectedQuestionMode);
    }

    private MathBomberConfig configFor(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return MathBomberConfig.easyConfig();
            case HARD:
                return MathBomberConfig.hardConfig();
            case MEDIUM:
            default:
                return MathBomberConfig.mediumConfig();
        }
    }

    private boolean handleRegisteredButtonClick(float x, float y) {
        for (MenuButtonBinding binding : buttonBindings) {
            if (binding.button.handleClick(x, y)) return true;
        }
        return false;
    }

    private Button registerActionButton(String label,
                                        float x,
                                        float y,
                                        float width,
                                        float height,
                                        Color defaultColor,
                                        Runnable action) {
        Button button = new Button(label, x, y, width, height, defaultColor, action);
        buttonBindings.add(new MenuButtonBinding(button, null));
        return button;
    }

    private Button registerToggleButton(String label,
                                        float x,
                                        float y,
                                        float width,
                                        float height,
                                        Runnable action,
                                        BooleanSupplier isSelected) {
        Button button = new Button(label, x, y, width, height, TOGGLE_NORMAL, action);
        buttonBindings.add(new MenuButtonBinding(button, isSelected));
        return button;
    }

    private static class MenuButtonBinding {
        private final Button button;
        private final BooleanSupplier isSelected;

        private MenuButtonBinding(Button button, BooleanSupplier isSelected) {
            this.button = button;
            this.isSelected = isSelected;
        }
    }

    private enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    @FunctionalInterface
    public interface StartGameHandler {
        void start(MathBomberConfig config, ControlScheme controls, QuestionMode questionMode);
    }
}
