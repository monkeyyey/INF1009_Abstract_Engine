package com.myGame.simulation.mathBomberScenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.simulation.entities.ExplosionCell;
import com.myGame.simulation.entities.MathBomb;
import com.myGame.simulation.entities.MathEnemy;
import com.myGame.simulation.entities.MathPlayer;
import com.myGame.engine.Animation.AnimationManager;
import com.myGame.engine.AudioManagement.AudioManager;
import com.myGame.engine.Collision.CollisionManager;
import com.myGame.engine.Collision.LifecycleManager;
import com.myGame.engine.EntityManagement.EntityManager;
import com.myGame.engine.InputManagement.InputManager;
import com.myGame.engine.InputManagement.InputState;
import com.myGame.engine.InputManagement.Interfaces.InputSource;
import com.myGame.engine.MovementManagement.MovementManager;
import com.myGame.engine.SceneManagement.abstractScenes.ActionScene;
import com.myGame.simulation.input.KeyboardArrowInputSource;
import com.myGame.simulation.input.KeyboardWASDInputSource;
import com.myGame.simulation.input.MouseInputSource;
import com.myGame.simulation.mathbomber.question.ArithmeticQuestionGenerator;
import com.myGame.simulation.mathbomber.config.ControlScheme;
import com.myGame.simulation.mathbomber.round.ExplosionOutcome;
import com.myGame.simulation.mathbomber.animation.MathBomberAnimationFactory;
import com.myGame.simulation.mathbomber.board.MathBoard;
import com.myGame.simulation.mathbomber.config.MathBomberConfig;
import com.myGame.simulation.mathbomber.factory.MathBomberFactory;
import com.myGame.simulation.mathbomber.factory.MathBomberFactoryProducer;
import com.myGame.simulation.mathbomber.config.MathBomberGameStats;
import com.myGame.simulation.mathbomber.round.MathRoundEntities;
import com.myGame.simulation.mathbomber.round.MathRoundState;
import com.myGame.simulation.mathbomber.config.QuestionMode;
import com.myGame.simulation.ui.GameFontFactory;

import java.util.List;
import java.util.function.Consumer;

public class GameScene extends ActionScene {
    private static final int INPUT_ID = 20;

    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final MathBomberConfig config;
    private final ControlScheme controlScheme;
    private final QuestionMode questionMode;
    private final Consumer<MathBomberGameStats> onGameWon;
    private final Consumer<MathBomberGameStats> onGameLost;
    private final ArithmeticQuestionGenerator questionGenerator;
    private final MathBomberFactory factory;
    private final MathBomberAnimationFactory animationFactory;

    private final MathRoundEntities roundEntities;
    private final ExplosionOutcome explosionOutcome = new ExplosionOutcome();
    private final MathRoundState roundState;

    private MathBoard board;
    private MathPlayer player;
    private BitmapFont hudFont;
    private BitmapFont statsFont;
    private BitmapFont titleFont;
    private BitmapFont enemyValueFont;
    private GlyphLayout hudLayout;
    private ArithmeticQuestionGenerator.ArithmeticQuestion currentQuestion;

    private boolean initialized;
    private boolean penaltyProcessedThisFrame;
    private boolean winTransitionTriggered;
    private boolean loseTransitionTriggered;
    private float elapsedGameSeconds;
    private Runnable pendingTransition;
    private float pendingTransitionDelay;

    public GameScene(InputManager inputManager,
                           AudioManager audioManager,
                           EntityManager entityManager,
                           CollisionManager collisionManager,
                           MovementManager movementManager,
                           LifecycleManager lifecycleManager,
                           AnimationManager animationManager,
                           MathBomberConfig config,
                           ControlScheme controlScheme,
                           QuestionMode questionMode,
                           Consumer<MathBomberGameStats> onGameWon,
                           Consumer<MathBomberGameStats> onGameLost) {
        this(inputManager, audioManager, entityManager, collisionManager, movementManager, lifecycleManager, animationManager, config, controlScheme, questionMode, onGameWon, onGameLost,
                MathBomberFactoryProducer.getFactory(config, questionMode));
    }

    public GameScene(InputManager inputManager,
                           AudioManager audioManager,
                           EntityManager entityManager,
                           CollisionManager collisionManager,
                           MovementManager movementManager,
                           LifecycleManager lifecycleManager,
                           AnimationManager animationManager,
                           MathBomberConfig config,
                           ControlScheme controlScheme,
                           QuestionMode questionMode,
                           Consumer<MathBomberGameStats> onGameWon,
                           Consumer<MathBomberGameStats> onGameLost,
                           MathBomberFactory factory) {
        super(entityManager, collisionManager, movementManager, lifecycleManager, animationManager);
        this.inputManager = inputManager;
        this.audioManager = audioManager;
        this.config = config;
        this.controlScheme = controlScheme;
        this.questionMode = questionMode == null ? QuestionMode.BOTH : questionMode;
        this.onGameWon = onGameWon;
        this.onGameLost = onGameLost;
        this.factory = factory;
        this.animationFactory = new MathBomberAnimationFactory();
        this.questionGenerator = factory.createQuestionGenerator();
        this.roundEntities = new MathRoundEntities(entityManager);
        this.roundState = new MathRoundState(
                config.winScore,
                config.bombsPerQuestion,
                config.questionTimeSeconds,
                config.startingLives);
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            initialized = true;
            hudFont = GameFontFactory.regular(22);
            statsFont = GameFontFactory.regular(20);
            titleFont = GameFontFactory.bold(28);
            enemyValueFont = GameFontFactory.regular(18);
            hudLayout = new GlyphLayout();
            board = factory.createBoard(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            player = factory.createPlayer(board, animationFactory.createPlayerAnimationComponent());
            player.setOccupancyChecker(this::canOccupyPlayer);
            player.setExplosionHitHandler(explosionOutcome::markPlayerHit);
            entityManager.addEntity("math_player", player);
        }
        InputSource inputSource;
        switch (controlScheme) {
            case WASD:
                inputSource = new KeyboardWASDInputSource();
                break;
            case MOUSE:
                inputSource = new MouseInputSource(player);
                break;
            case ARROW_KEYS:
            default:
                inputSource = new KeyboardArrowInputSource();
                break;
        }
        inputManager.addInputSource(INPUT_ID, inputSource);

        audioManager.playMusic("calm");
        resetWholeGame();
    }

    @Override
    public void onExit() {
        inputManager.removeInputSource(INPUT_ID);
        pendingTransition = null;
        pendingTransitionDelay = 0f;
    }

    @Override
    public void update(float dt) {
        InputState input = inputManager.getState(INPUT_ID);
        if (input == null) {
            super.update(dt);
            return;
        }

        if (pendingTransition != null) {
            updateExplosionCells();
            super.update(dt);
            roundEntities.removeInactiveEnemies();
            pendingTransitionDelay -= dt;
            if (pendingTransitionDelay <= 0f) {
                Runnable transition = pendingTransition;
                pendingTransition = null;
                pendingTransitionDelay = 0f;
                transition.run();
            }
            return;
        }

        penaltyProcessedThisFrame = false;
        explosionOutcome.resetFrame();

        if (roundState.isGameWon()) {
            if (!winTransitionTriggered) {
                dispatchWinTransition();
                if (winTransitionTriggered) {
                    return;
                }
            }
            if (input.isQuit()) {
                Gdx.app.exit();
                return;
            }
            if (input.isAction2JustPressed()) {
                resetWholeGame();
            }
            super.update(dt);
            return;
        }

        if (input.isAction2JustPressed()) {
            resetWholeGame();
        }

        elapsedGameSeconds += dt;
        roundState.tick(dt);
        if (roundState.isTimeUp()) {
            startQuestionRound("Time up! New question.", 1.8f, true);
        }

        player.applyInput(input);
        if (input.isAction1JustPressed()) {
            placeBombAtPlayer();
        }

        updateBombs();
        updateExplosionCells();
        updateEnemyIntents();

        super.update(dt);
        roundEntities.removeInactiveEnemies();

        if (!explosionOutcome.isExplosionTriggered()) return;

        if (explosionOutcome.isPlayerHit()) {
            applyLifePenaltyAndMaybeRefresh("You hit yourself. New question.");
            return;
        }
        if (explosionOutcome.isCorrectEnemyHit()) {
            playSoundSafely("ding");
            boolean won = roundState.markCorrectAnswer();
            if (won) {
                roundState.setFeedback("All questions solved.", 0f);
                clearRoundEntities();
                scheduleTransition(this::dispatchWinTransition);
            } else {
                roundState.setFeedback("Correct! Next question.", 1.5f);
                scheduleTransition(() -> startQuestionRound("Correct! Next question.", 1.5f, false));
            }
            return;
        }
        if (explosionOutcome.isWrongEnemyHit()) {
            playSoundSafely("failure");
            if (roundState.getBombsRemaining() <= 0) {
                applyPenaltyAndRefresh("Out of bombs. New question.");
            } else {
                roundState.setFeedback("Wrong target. Try again.", 1.2f);
            }
            return;
        }
        if (roundState.getBombsRemaining() <= 0) {
            applyPenaltyAndRefresh("Out of bombs. New question.");
        } else {
            roundState.setFeedback("No hit. Try again.", 1.2f);
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shape) {
        board.draw(shape, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shape.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.drawShapes(shape);
        shape.end();

        batch.begin();
        entityManager.drawSprites(batch);
        float topY = Gdx.graphics.getHeight() - 16f;
        float lineGap = 34f;
        hudFont.setColor(Color.WHITE);
        statsFont.setColor(Color.WHITE);
        titleFont.setColor(Color.GREEN);

        titleFont.draw(batch, modeTitle(), 16f, topY);

        String question = "Question: " + currentQuestion.getLeft() + " " + currentQuestion.getOperatorSymbol() + " " + currentQuestion.getRight() + " = ?";
        hudLayout.setText(hudFont, question);
        float qx = (Gdx.graphics.getWidth() - hudLayout.width) * 0.5f;
        hudFont.draw(batch, hudLayout, qx, topY - lineGap);

        String statsLine = "Score: " + roundState.getScore() + " / " + config.winScore
                + "   Lives: " + roundState.getLivesRemaining()
                + "   Time: " + Math.max(0, (int) Math.ceil(roundState.getQuestionTimeLeft())) + "s"
                + "   Bombs Left: " + roundState.getBombsRemaining()
                + "   Total Elapsed: " + formatElapsedTime(elapsedGameSeconds);
        hudLayout.setText(statsFont, statsLine);
        float sx = (Gdx.graphics.getWidth() - hudLayout.width) * 0.5f;
        statsFont.draw(batch, hudLayout, sx, topY - lineGap * 2f);

        String controlsLine = "Move: " + moveHint() + "   Bomb: " + bombHint() + "   Reset: R";
        hudLayout.setText(hudFont, controlsLine);
        float cx = (Gdx.graphics.getWidth() - hudLayout.width) * 0.5f;
        hudFont.draw(batch, hudLayout, cx, topY - lineGap * 3f);

        if (roundState.isGameWon()) {
            String winText = "You win! Press R to play again or Q to quit.";
            hudLayout.setText(hudFont, winText);
            float wx = (Gdx.graphics.getWidth() - hudLayout.width) * 0.5f;
            hudFont.setColor(Color.GREEN);
            hudFont.draw(batch, hudLayout, wx, topY - lineGap * 4f);
        } else if (!roundState.getFeedbackText().isEmpty()) {
            String feedback = roundState.getFeedbackText();
            hudLayout.setText(hudFont, feedback);
            float fx = (Gdx.graphics.getWidth() - hudLayout.width) * 0.5f;
            hudFont.setColor(feedback.startsWith("Correct") ? Color.GREEN : Color.RED);
            hudFont.draw(batch, hudLayout, fx, topY - lineGap * 4f);
        }

        for (MathEnemy enemy : roundEntities.enemySnapshot()) {
            if (!enemy.isActive()) continue;
            String valueText = String.valueOf(enemy.getValue());
            hudLayout.setText(enemyValueFont, valueText);
            float tx = enemy.getX() - hudLayout.width * 0.5f;
            float ty = enemy.getY() + hudLayout.height * 0.5f;
            enemyValueFont.setColor(Color.WHITE);
            enemyValueFont.draw(batch, hudLayout, tx, ty);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
        if (hudFont != null) {
            hudFont.dispose();
        }
        if (statsFont != null) {
            statsFont.dispose();
        }
        if (titleFont != null) {
            titleFont.dispose();
        }
        if (enemyValueFont != null) {
            enemyValueFont.dispose();
        }
        animationFactory.dispose();
    }

    private void resetWholeGame() {
        roundState.resetWholeGame();
        winTransitionTriggered = false;
        loseTransitionTriggered = false;
        elapsedGameSeconds = 0f;
        pendingTransition = null;
        pendingTransitionDelay = 0f;
        startQuestionRound(null, 0f, false);
    }

    private void startQuestionRound(String feedback, float feedbackSeconds, boolean applyScorePenalty) {
        roundState.startQuestion(feedback, feedbackSeconds, applyScorePenalty);
        clearRoundEntities();
        player.setGridPosition(config.playerSpawnRow, config.playerSpawnCol);

        currentQuestion = questionGenerator.nextQuestion();
        spawnQuestionEnemies();
    }

    private void spawnQuestionEnemies() {
        List<Integer> values = questionGenerator.buildEnemyValues(
                currentQuestion.getAnswer(),
                config.enemyCount,
                currentQuestion.getOperation());
        for (int value : values) {
            int row;
            int col;
            do {
                row = MathUtils.random(1, board.getRows() - 2);
                col = MathUtils.random(1, board.getCols() - 2);
            } while (!canSpawnEnemyAt(row, col));

            MathEnemy enemy = factory.createEnemy(board, row, col, value);
            enemy.setPlayerCollisionHandler(() -> {
                playSoundSafely("beep");
                applyLifePenaltyAndMaybeRefresh("Enemy touched you. New question.");
            });
            enemy.setExplosionHitHandler(e -> {
                if (e.getValue() == currentQuestion.getAnswer()) {
                    explosionOutcome.markCorrectEnemyHit();
                } else {
                    explosionOutcome.markWrongEnemyHit();
                }
            });

            List<MathBomberConfig.Direction> directions = MathBomberConfig.cardinalDirections();
            MathBomberConfig.Direction dir = directions.get(MathUtils.random(0, directions.size() - 1));
            enemy.setLastDirection(dir.getRowDelta(), dir.getColDelta());
            roundEntities.addEnemy("math_enemy_" + System.nanoTime(), enemy);
        }
    }

    private void clearRoundEntities() {
        roundEntities.clear();
    }

    private void updateEnemyIntents() {
        for (MathEnemy enemy : roundEntities.enemySnapshot()) {
            if (!enemy.isActive() || enemy.isMoving()) continue;

            int currentDr = enemy.getLastDirRow();
            int currentDc = enemy.getLastDirCol();
            int[][] dirs = new int[][] {{currentDr, currentDc}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};

            boolean moved = false;
            for (int[] dir : dirs) {
                if (dir[0] == 0 && dir[1] == 0) continue;
                int nr = enemy.getRow() + dir[0];
                int nc = enemy.getCol() + dir[1];
                if (canEnemyMoveTo(nr, nc, enemy)) {
                    enemy.setLastDirection(dir[0], dir[1]);
                    enemy.startMoveTo(nr, nc, config.enemyMoveDurationSeconds);
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                enemy.setLastDirection(-currentDr, -currentDc);
            }
        }
    }

    private void updateBombs() {
        List<MathBomb> snapshot = roundEntities.bombSnapshot();
        for (MathBomb bomb : snapshot) {
            if (!roundEntities.containsBomb(bomb) || !bomb.isExpired()) continue;

            playSoundSafely("explosion");
            explosionOutcome.markExplosionTriggered();
            List<MathBoard.BlastCell> blastCells = board.computeBlastCells(bomb.getRow(), bomb.getCol(), bomb.getBlastRange());

            for (MathBoard.BlastCell cell : blastCells) {
                ExplosionCell exp = factory.createExplosion(board, cell.getRow(), cell.getCol(), config.explosionDurationSeconds);
                roundEntities.addExplosion("math_explosion_" + System.nanoTime(), exp);
            }

            roundEntities.removeBomb(bomb);
        }
    }

    private void updateExplosionCells() {
        roundEntities.removeExpiredExplosions();
    }

    private void placeBombAtPlayer() {
        if (roundState.getBombsRemaining() <= 0) return;

        int bombRow = player.getRow();
        int bombCol = player.getCol();
        if (!board.isInside(bombRow, bombCol) || board.isWall(bombRow, bombCol)) return;
        if (roundEntities.hasBombAt(bombRow, bombCol)) return;

        roundState.consumeBomb();
        MathBomb bomb = factory.createBomb(
                board,
                bombRow,
                bombCol,
                config.bombFuseSeconds,
                config.bombBlastRange,
                animationFactory.createBombAnimationComponent());
        roundEntities.addBomb("math_bomb_" + System.nanoTime(), bomb);
    }

    private void applyPenaltyAndRefresh(String message) {
        if (penaltyProcessedThisFrame) return;
        penaltyProcessedThisFrame = true;
        startQuestionRound(message, 1.8f, true);
    }

    private void applyLifePenaltyAndMaybeRefresh(String message) {
        if (penaltyProcessedThisFrame) return;
        penaltyProcessedThisFrame = true;
        boolean stillAlive = roundState.consumeLife();
        if (!stillAlive) {
            roundState.setFeedback("Out of lives.", 0f);
            clearRoundEntities();
            scheduleTransition(this::dispatchLoseTransition);
            return;
        }
        startQuestionRound(message, 1.8f, true);
    }

    private boolean canSpawnEnemyAt(int row, int col) {
        if (!board.isInside(row, col) || board.isWall(row, col)) return false;
        int manhattanDistance = Math.abs(player.getRow() - row) + Math.abs(player.getCol() - col);
        if (manhattanDistance < 3) return false;
        if (roundEntities.hasBombAt(row, col)) return false;
        return !roundEntities.hasActiveEnemyAt(row, col);
    }

    private boolean canEnemyMoveTo(int row, int col, MathEnemy movingEnemy) {
        if (!board.isInside(row, col) || board.isWall(row, col)) return false;
        if (roundEntities.hasBombAt(row, col)) return false;
        return !roundEntities.hasActiveEnemyAtExcept(row, col, movingEnemy);
    }

    private boolean canOccupyPlayer(float candidateX, float candidateY, float radius) {
        MathBoard.TileBounds candidateBounds = board.circleBoundsAt(candidateX, candidateY, radius);
        MathBoard.TileBounds currentBounds = board.circleBoundsAt(player.getX(), player.getY(), radius);

        return board.allTilesPass(candidateBounds, (row, col) -> {
            if (!board.isInside(row, col)) return false;
            if (board.isWall(row, col)) return false;
            if (roundEntities.hasBombAt(row, col) && !board.tileBoundsContain(currentBounds, row, col)) return false;
            return true;
        });
    }

    private String moveHint() {
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

    private String bombHint() {
        return controlScheme == ControlScheme.MOUSE ? "Left Click" : "SPACE";
    }

    private String modeTitle() {
        switch (questionMode) {
            case ADDITION_ONLY:
                return "Addition Game";
            case MULTIPLICATION_ONLY:
                return "Multiplication Game";
            case BOTH:
            default:
                return "Math Bomber";
        }
    }

    private void playSoundSafely(String key) {
        try {
            audioManager.playSound(key);
        } catch (IllegalArgumentException ignored) {
            // If a sound key is missing, continue without crashing.
        }
    }

    private void scheduleTransition(Runnable transition) {
        pendingTransition = transition;
        pendingTransitionDelay = Math.max(0.1f, config.explosionDurationSeconds);
    }

    private void dispatchWinTransition() {
        if (onGameWon == null || winTransitionTriggered) {
            return;
        }
        winTransitionTriggered = true;
        onGameWon.accept(buildCurrentStats());
    }

    private void dispatchLoseTransition() {
        if (onGameLost == null || loseTransitionTriggered) {
            return;
        }
        loseTransitionTriggered = true;
        onGameLost.accept(buildCurrentStats());
    }

    public MathBomberGameStats buildCurrentStats() {
        return new MathBomberGameStats(config.difficultyName, elapsedGameSeconds, roundState.getScore());
    }

    private String formatElapsedTime(float seconds) {
        int total = Math.max(0, Math.round(seconds));
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
