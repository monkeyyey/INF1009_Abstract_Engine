package com.myGame.game.scenes.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.enums.ControlScheme;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;
import com.myGame.game.mathbomber.questions.ArithmeticQuestionGenerator;
import com.myGame.game.mathbomber.round.RoundEntities;
import com.myGame.game.mathbomber.round.RoundState;
import com.myGame.game.ui.GameFontFactory;

public class GameHudRenderer {
    private final BitmapFont hudFont;
    private final BitmapFont statsFont;
    private final BitmapFont titleFont;
    private final BitmapFont enemyValueFont;
    private final GlyphLayout layout;

    public GameHudRenderer() {
        this.hudFont = GameFontFactory.regular(22);
        this.statsFont = GameFontFactory.regular(18);
        this.titleFont = GameFontFactory.bold(28);
        this.enemyValueFont = GameFontFactory.regular(18);
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch,
                       QuestionMode questionMode,
                       ControlScheme controlScheme,
                       ArithmeticQuestionGenerator.ArithmeticQuestion currentQuestion,
                       RoundState roundState,
                       MathBomberConfig config,
                       float elapsedGameSeconds,
                       RoundEntities roundEntities) {
        float topY = Gdx.graphics.getHeight() - 16f;
        float lineGap = 34f;
        hudFont.setColor(Color.WHITE);
        statsFont.setColor(Color.WHITE);
        titleFont.setColor(Color.GREEN);

        titleFont.draw(batch, modeTitle(questionMode), 16f, topY);

        String question = "Question: " + currentQuestion.getLeft() + " "
                + currentQuestion.getOperatorSymbol() + " "
                + currentQuestion.getRight() + " = ?";
        layout.setText(hudFont, question);
        float questionX = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        hudFont.draw(batch, layout, questionX, topY - lineGap);

        String statsLine = "Score: " + roundState.getScore() + " / " + config.winScore
                + "   Lives: " + roundState.getLivesRemaining()
                + "   Time: " + Math.max(0, (int) Math.ceil(roundState.getQuestionTimeLeft())) + "s"
                + "   Bombs Left: " + roundState.getBombsRemaining()
                + "   Total Elapsed: " + formatElapsedTime(elapsedGameSeconds);
        layout.setText(statsFont, statsLine);
        float statsX = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        statsFont.draw(batch, layout, statsX, topY - lineGap * 2f);

        String controlsLine = "Move: " + moveHint(controlScheme) + "   Bomb: " + bombHint(controlScheme);
        layout.setText(hudFont, controlsLine);
        float controlsX = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        hudFont.draw(batch, layout, controlsX, topY - lineGap * 3f);

        if (roundState.isGameWon()) {
            drawCenteredFeedback(batch, "You win!", Color.GREEN, topY - lineGap * 4f);
        } else if (!roundState.getFeedbackText().isEmpty()) {
            String feedback = roundState.getFeedbackText();
            drawCenteredFeedback(
                    batch,
                    feedback,
                    "Correct! Next question.".equals(feedback) ? Color.GREEN : Color.RED,
                    topY - lineGap * 4f);
        }

        roundEntities.forEachEnemy(enemy -> {
            if (!enemy.isActive()) {
                return;
            }
            String valueText = String.valueOf(enemy.getValue());
            layout.setText(enemyValueFont, valueText);
            float tx = enemy.getX() - layout.width * 0.5f;
            float ty = enemy.getY() + layout.height * 0.5f;
            enemyValueFont.setColor(Color.WHITE);
            enemyValueFont.draw(batch, layout, tx, ty);
        });
    }

    public void dispose() {
        hudFont.dispose();
        statsFont.dispose();
        titleFont.dispose();
        enemyValueFont.dispose();
    }

    private void drawCenteredFeedback(SpriteBatch batch, String text, Color color, float y) {
        hudFont.setColor(color);
        layout.setText(hudFont, text);
        float x = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        hudFont.draw(batch, layout, x, y);
    }

    private String moveHint(ControlScheme controlScheme) {
        switch (controlScheme) {
            case WASD:
                return "WASD";
            case MOUSE:
                return "Mouse";
            case ARROW_KEYS:
            default:
                return "Arrow Keys";
        }
    }

    private String bombHint(ControlScheme controlScheme) {
        return controlScheme == ControlScheme.MOUSE ? "Left Click" : "SPACE";
    }

    private String modeTitle(QuestionMode questionMode) {
        switch (questionMode) {
            case ADDITION_ONLY:
                return "Addition Game";
            case MULTIPLICATION_ONLY:
                return "Multiplication Game";
            case BOTH:
                return "Mixed Game";
            default:
                return "Math Bomber";
        }
    }

    private String formatElapsedTime(float seconds) {
        int total = Math.max(0, Math.round(seconds));
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
