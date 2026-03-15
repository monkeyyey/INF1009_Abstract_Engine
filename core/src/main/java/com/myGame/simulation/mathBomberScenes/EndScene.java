package com.myGame.simulation.mathBomberScenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.myGame.engine.EntityManagement.EntityManager;
import com.myGame.engine.InputManagement.InputManager;
import com.myGame.engine.InputManagement.Interfaces.InputState;
import com.myGame.engine.SceneManagement.abstractScenes.StaticScene;
import com.myGame.simulation.input.MouseClickInputSource;
import com.myGame.simulation.mathbomber.config.MathBomberGameStats;
import com.myGame.simulation.ui.Button;
import com.myGame.simulation.ui.GameFontFactory;

public class EndScene extends StaticScene {
    private static final int POINTER_INPUT_ID = 301;

    private final InputManager inputManager;
    private final MathBomberGameStats stats;
    private final boolean didWin;
    private final Runnable onQuit;
    private final Runnable onBackToMenu;

    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyph;
    private boolean initialized;
    private Button quitButton;
    private Button backButton;

    public EndScene(InputManager inputManager,
                              MathBomberGameStats stats,
                              boolean didWin,
                              Runnable onQuit,
                              Runnable onBackToMenu,
                              EntityManager entityManager) {
        super(entityManager);
        this.inputManager = inputManager;
        this.stats = stats;
        this.didWin = didWin;
        this.onQuit = onQuit;
        this.onBackToMenu = onBackToMenu;
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            font = GameFontFactory.regular(24);
            titleFont = GameFontFactory.bold(72);
            glyph = new GlyphLayout();
            initialized = true;
        }
        buildUi();
        inputManager.addInputSource(POINTER_INPUT_ID, new MouseClickInputSource());
    }

    @Override
    public void onExit() {
        inputManager.removeInputSource(POINTER_INPUT_ID);
    }

    @Override
    public void update(float dt) {
        InputState pointer = inputManager.getState(POINTER_INPUT_ID);
        if (pointer == null || !pointer.isJustTouched()) return;

        float x = pointer.getPointerX();
        float y = pointer.getPointerY();
        if (quitButton.handleClick(x, y)) return;
        backButton.handleClick(x, y);
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0.05f, 0.12f, 0.08f, 1f);
        shape.rect(0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        quitButton.draw(shape);
        backButton.draw(shape);
        shape.end();

        batch.begin();
        String title = didWin ? "You Won!" : "You Lost!";
        titleFont.setColor(didWin ? Color.LIME : Color.RED);
        glyph.setText(titleFont, title);
        float titleX = (Gdx.graphics.getWidth() - glyph.width) * 0.5f;
        float topY = Gdx.graphics.getHeight() * 0.58f + 28f;
        titleFont.draw(batch, glyph, titleX, topY);

        font.setColor(Color.WHITE);
        drawCenteredLine(batch, "Difficulty: " + stats.getDifficultyName(), topY - 80f);
        drawCenteredLine(batch, "Time Taken: " + formatSeconds(stats.getElapsedSeconds()), topY - 114f);
        drawCenteredLine(batch, "Score: " + stats.getScore(), topY - 148f);

        drawCenteredLabel(batch, quitButton);
        drawCenteredLabel(batch, backButton);
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
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float buttonW = Math.min(280f, w * 0.35f);
        float buttonH = Math.max(50f, h * 0.08f);
        float gap = 26f;
        float startX = (w - (buttonW * 2f + gap)) * 0.5f;
        float y = h * 0.22f;

        backButton = new Button("Back To Menu", startX, y, buttonW, buttonH, Color.ROYAL, onBackToMenu);
        quitButton = new Button("Quit", startX + buttonW + gap, y, buttonW, buttonH, Color.FIREBRICK, onQuit);
    }

    private void drawCenteredLabel(SpriteBatch batch, Button button) {
        glyph.setText(font, button.getLabel());
        float tx = button.getX() + (button.getWidth() - glyph.width) * 0.5f;
        float ty = button.getY() + (button.getHeight() + glyph.height) * 0.5f;
        font.draw(batch, glyph, tx, ty);
    }

    private void drawCenteredLine(SpriteBatch batch, String text, float y) {
        glyph.setText(font, text);
        float x = (Gdx.graphics.getWidth() - glyph.width) * 0.5f;
        font.draw(batch, glyph, x, y);
    }

    private String formatSeconds(float seconds) {
        int total = Math.max(0, Math.round(seconds));
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
