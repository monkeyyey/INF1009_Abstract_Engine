package com.myGame.simulation.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.core.InputState;
import com.myGame.engine.managers.AudioManager;
import com.myGame.engine.managers.EntityManager;
import com.myGame.engine.managers.InputManager;
import com.myGame.engine.physics.CollisionManager;
import com.myGame.engine.physics.MovementManager;
import com.myGame.engine.scenes.Scene;
import com.myGame.simulation.input.KeyboardCustomInputSource;
import com.myGame.simulation.input.MouseClickInputSource;
import com.myGame.simulation.mathbomber.config.ControlScheme;
import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.config.QuestionMode;
import com.myGame.simulation.ui.Button;
import com.myGame.simulation.ui.GameFontFactory;
import com.myGame.simulation.ui.VolumeSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class MathBomberStartMenuScene extends Scene {
    private static final int MENU_POINTER_INPUT_ID = 201;
    private static final int MENU_KEYBOARD_INPUT_ID = 202;
    private static final Color TOGGLE_SELECTED = Color.ROYAL;
    private static final Color TOGGLE_NORMAL = Color.DARK_GRAY;

    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final StartGameHandler onStart;
    private final Runnable onQuit;

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyph;
    private boolean initialized;

    private Button startButton;
    private Button quitButton;
    private VolumeSlider volumeSlider;
    private final List<MenuButtonBinding> buttonBindings = new ArrayList<>();
    private float difficultyHeaderCenterX;
    private float difficultyHeaderY;
    private float controlsHeaderCenterX;
    private float controlsHeaderY;
    private float questionsHeaderCenterX;
    private float questionsHeaderY;

    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private ControlScheme selectedControls = ControlScheme.ARROW_KEYS;
    private QuestionMode selectedQuestionMode = QuestionMode.BOTH;

    public MathBomberStartMenuScene(InputManager inputManager,
                                    AudioManager audioManager,
                                    StartGameHandler onStart,
                                    Runnable onQuit,
                                    EntityManager entityManager,
                                    CollisionManager collisionManager,
                                    MovementManager movementManager) {
        super(entityManager, collisionManager, movementManager);
        this.inputManager = inputManager;
        this.audioManager = audioManager;
        this.onStart = onStart;
        this.onQuit = onQuit;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            font = GameFontFactory.regular(24);
            titleFont = GameFontFactory.boldWithBorder(56, 2f, new Color(0f, 0f, 0f, 0.35f));
            glyph = new GlyphLayout();
            initialized = true;
        }
        buildUi();
        inputManager.addInputSource(MENU_POINTER_INPUT_ID, new MouseClickInputSource());
        inputManager.addInputSource(MENU_KEYBOARD_INPUT_ID, new KeyboardCustomInputSource(
                com.badlogic.gdx.Input.Keys.LEFT,
                com.badlogic.gdx.Input.Keys.RIGHT,
                -1,
                -1,
                com.badlogic.gdx.Input.Keys.ENTER,
                -1,
                -1));
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

        if (keyboard.isLeft()) {
            setVolume(volumeSlider.getValue() - dt * 0.6f);
        } else if (keyboard.isRight()) {
            setVolume(volumeSlider.getValue() + dt * 0.6f);
        }

        if (keyboard.isAction1JustPressed()) {
            startGame();
            return;
        }

        if (!pointer.isJustTouched()) return;

        float x = pointer.getPointerX();
        float y = pointer.getPointerY();
        if (handleRegisteredButtonClick(x, y)) return;
        if (volumeSlider.contains(x, y)) {
            float relative = (x - volumeSlider.getX()) / volumeSlider.getWidth();
            setVolume(relative);
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
        volumeSlider.draw(shape);
        shape.end();

        batch.begin();
        font.setColor(Color.WHITE);
        String title = "Math Bomber";
        glyph.setText(titleFont, title);
        float titleX = (Gdx.graphics.getWidth() - glyph.width) * 0.5f;
        float titleY = Gdx.graphics.getHeight() - 94f;
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

        String questionsHeader = "Choose Questions";
        glyph.setText(font, questionsHeader);
        font.draw(batch, glyph, questionsHeaderCenterX - glyph.width * 0.5f, questionsHeaderY);

        String volumeText = "Music Volume: " + Math.round(volumeSlider.getValue() * 100f) + "%";
        glyph.setText(font, volumeText);
        float volumeX = volumeSlider.getX() + (volumeSlider.getWidth() - glyph.width) * 0.5f;
        font.draw(batch, glyph, volumeX, volumeSlider.getY() + 40f);
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

        float row1Y = h * 0.62f;
        float row2Y = h * 0.50f;
        float row3Y = h * 0.38f;
        float startY = h * 0.16f;
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

        float controlW = Math.min(260f, w * 0.3f);
        float controlGap = 30f;
        float controlStartX = (w - (controlW * 2f + controlGap)) / 2f;
        registerToggleButton(
                "Arrow Keys", controlStartX, row2Y, controlW, buttonH,
                () -> selectedControls = ControlScheme.ARROW_KEYS,
                () -> selectedControls == ControlScheme.ARROW_KEYS);
        registerToggleButton(
                "WASD", controlStartX + controlW + controlGap, row2Y, controlW, buttonH,
                () -> selectedControls = ControlScheme.WASD,
                () -> selectedControls == ControlScheme.WASD);

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
                "Start Game", (w * 0.5f) - startW - 16f, startY, startW, buttonH,
                Color.FOREST, this::startGame);
        quitButton = registerActionButton(
                "Quit", (w * 0.5f) + 16f, startY, startW, buttonH,
                Color.FIREBRICK, onQuit);

        float sliderW = Math.min(420f, w * 0.5f);
        volumeSlider = new VolumeSlider((w - sliderW) / 2f, h * 0.27f, sliderW, 18f, Color.DARK_GRAY, Color.SKY, currentMenuVolume());
        difficultyHeaderCenterX = leftX + (buttonW * 3f + gap * 2f) * 0.5f;
        difficultyHeaderY = row1Y + buttonH + 28f;
        controlsHeaderCenterX = controlStartX + (controlW * 2f + controlGap) * 0.5f;
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
        glyph.setText(font, button.getLabel());
        float tx = button.getX() + (button.getWidth() - glyph.width) * 0.5f;
        float ty = button.getY() + (button.getHeight() + glyph.height) * 0.5f;
        font.draw(batch, glyph, tx, ty);
    }

    private void setVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        volumeSlider.setValue(clamped);
        audioManager.setMusicTrackVolume("intense", clamped);
        audioManager.setMusicTrackVolume("calm", clamped);
        audioManager.setMusicVolume(clamped);
    }

    private float currentMenuVolume() {
        float current = audioManager.getMusicVolume();
        return current > 0f ? current : 0.35f;
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
