package com.myGame.game.scenes.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.myGame.engine.animation.AnimationManager;
import com.myGame.engine.audio.AudioManager;
import com.myGame.engine.collision.CollisionManager;
import com.myGame.engine.lifecycle.LifecycleManager;
import com.myGame.engine.entity.EntityManager;
import com.myGame.engine.input.InputManager;
import com.myGame.engine.input.InputSource;
import com.myGame.engine.input.InputState;
import com.myGame.engine.movement.MovementManager;
import com.myGame.engine.scene.ActionScene;
import com.myGame.game.animation.AnimationFactory;
import com.myGame.game.entities.MathBomb;
import com.myGame.game.entities.MathEnemy;
import com.myGame.game.entities.MathPlayer;
import com.myGame.game.factory.MathBomberFactory;
import com.myGame.game.input.keyboard.KeyboardArrowInputSource;
import com.myGame.game.input.keyboard.KeyboardWASDInputSource;
import com.myGame.game.input.mouse.MouseInputSource;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.configurations.MathBomberGameStats;
import com.myGame.game.mathbomber.configurations.enums.ControlScheme;
import com.myGame.game.mathbomber.configurations.enums.QuestionMode;
import com.myGame.game.mathbomber.questions.ArithmeticQuestionGenerator;
import com.myGame.game.mathbomber.round.ExplosionOutcome;
import com.myGame.game.mathbomber.round.RoundEntities;
import com.myGame.game.mathbomber.round.RoundState;
import com.myGame.game.mathbomber.systems.BombExplosionSystem;
import com.myGame.game.mathbomber.systems.EnemySteeringSystem;
import com.myGame.game.mathbomber.systems.OccupancyPolicy;
import com.myGame.game.scenes.renderers.GameHudRenderer;

import java.util.List;
import java.util.function.Consumer;

public class GameScene extends ActionScene {
    private static final int INPUT_ID = 20;

    private enum SceneFlowState {
        PLAYING,
        WAITING_TRANSITION,
        TERMINAL
    }

    private static final class DelayedTransition {
        private final Runnable action;
        private float remainingSeconds;

        private DelayedTransition(Runnable action, float remainingSeconds) {
            this.action = action;
            this.remainingSeconds = remainingSeconds;
        }

        private boolean tick(float dt) {
            remainingSeconds -= dt;
            return remainingSeconds <= 0f;
        }
    }

    private static final class FrameResolutionGate {
        private boolean resolved;

        private void beginFrame() {
            resolved = false;
        }

        private boolean tryResolve() {
            if (resolved) {
                return false;
            }
            resolved = true;
            return true;
        }
    }

    private final InputManager inputManager;
    private final AudioManager audioManager;
    private final MathBomberConfig config;
    private final ControlScheme controlScheme;
    private final QuestionMode questionMode;
    private final Consumer<MathBomberGameStats> onGameWon;
    private final Consumer<MathBomberGameStats> onGameLost;
    private final ArithmeticQuestionGenerator questionGenerator;
    private final MathBomberFactory factory;
    private final AnimationFactory animationFactory;
    private final BombExplosionSystem bombExplosionSystem;

    private final RoundEntities roundEntities;
    private final ExplosionOutcome explosionOutcome = new ExplosionOutcome();
    private final RoundState roundState;

    private Board board;
    private MathPlayer player;
    private GameHudRenderer hudRenderer;
    private OccupancyPolicy occupancyPolicy;
    private EnemySteeringSystem enemySteeringSystem;
    private ArithmeticQuestionGenerator.ArithmeticQuestion currentQuestion;
    private final FrameResolutionGate frameResolutionGate = new FrameResolutionGate();

    private boolean initialized;
    private float elapsedGameSeconds;
    private SceneFlowState flowState = SceneFlowState.PLAYING;
    private DelayedTransition delayedTransition;

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
        this.animationFactory = new AnimationFactory();
        this.bombExplosionSystem = new BombExplosionSystem(factory, config, () -> playSoundSafely("explosion"));
        this.questionGenerator = factory.createQuestionGenerator();
        this.roundEntities = new RoundEntities();
        this.roundState = new RoundState(
                config.winScore,
                config.bombsPerQuestion,
                config.questionTimeSeconds,
                config.startingLives);
    }

    @Override
    public void onEnter() {
        if (!initialized) {
            initialized = true;
            hudRenderer = new GameHudRenderer();
            board = factory.createBoard(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            occupancyPolicy = new OccupancyPolicy(board, roundEntities);
            enemySteeringSystem = new EnemySteeringSystem(config, occupancyPolicy);
            player = factory.createPlayer(board, animationFactory.createPlayerAnimationComponent());
            occupancyPolicy.setPlayer(player);
            player.setOccupancyChecker(occupancyPolicy::canOccupyPlayer);
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
        delayedTransition = null;
        flowState = SceneFlowState.PLAYING;
    }

    @Override
    public void update(float dt) {
        if (flowState == SceneFlowState.WAITING_TRANSITION && delayedTransition != null) {
            updateDelayedTransition(dt);
            return;
        }

        InputState input = inputManager.getState(INPUT_ID);
        if (input == null) {
            super.update(dt);
            return;
        }

        frameResolutionGate.beginFrame();
        explosionOutcome.resetFrame();

        if (roundState.isGameWon()) {
            dispatchWinTransition();
            return;
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

        bombExplosionSystem.updateBombs(
                board,
                roundEntities,
                explosionOutcome,
                explosion -> entityManager.addEntity("math_explosion_" + System.nanoTime(), explosion));
        bombExplosionSystem.updateExplosions(roundEntities);
        enemySteeringSystem.update(roundEntities);

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
        hudRenderer.render(
                batch,
                questionMode,
                controlScheme,
                currentQuestion,
                roundState,
                config,
                elapsedGameSeconds,
                roundEntities);
        batch.end();
    }

    @Override
    public void dispose() {
        entityManager.dispose();
        if (hudRenderer != null) {
            hudRenderer.dispose();
        }
        animationFactory.dispose();
    }

    private void resetWholeGame() {
        roundState.resetWholeGame();
        elapsedGameSeconds = 0f;
        delayedTransition = null;
        flowState = SceneFlowState.PLAYING;
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
            } while (!occupancyPolicy.canSpawnEnemyAt(row, col));

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
            roundEntities.addEnemy(enemy);
            entityManager.addEntity("math_enemy_" + System.nanoTime(), enemy);
        }
    }

    private void clearRoundEntities() {
        roundEntities.clear();
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
        roundEntities.addBomb(bomb);
        entityManager.addEntity("math_bomb_" + System.nanoTime(), bomb);
    }

    private void applyPenaltyAndRefresh(String message) {
        if (!frameResolutionGate.tryResolve()) return;
        startQuestionRound(message, 1.8f, true);
    }

    private void applyLifePenaltyAndMaybeRefresh(String message) {
        if (!frameResolutionGate.tryResolve()) return;
        boolean stillAlive = roundState.consumeLife();
        if (!stillAlive) {
            roundState.setFeedback("Out of lives.", 0f);
            clearRoundEntities();
            scheduleTransition(this::dispatchLoseTransition);
            return;
        }
        startQuestionRound(message, 1.8f, true);
    }

    private void playSoundSafely(String key) {
        try {
            audioManager.playSound(key);
        } catch (IllegalArgumentException ignored) {
            // If a sound key is missing, continue without crashing.
        }
    }

    private void scheduleTransition(Runnable transition) {
        delayedTransition = new DelayedTransition(
                transition,
                Math.max(0.1f, config.explosionDurationSeconds));
        flowState = SceneFlowState.WAITING_TRANSITION;
    }

    private void dispatchWinTransition() {
        dispatchGameTransition(onGameWon);
    }

    private void dispatchLoseTransition() {
        dispatchGameTransition(onGameLost);
    }

    private void updateDelayedTransition(float dt) {
        bombExplosionSystem.updateExplosions(roundEntities);
        super.update(dt);
        roundEntities.removeInactiveEnemies();
        if (!delayedTransition.tick(dt)) {
            return;
        }
        Runnable action = delayedTransition.action;
        delayedTransition = null;
        action.run();
        if (flowState == SceneFlowState.WAITING_TRANSITION) {
            flowState = SceneFlowState.PLAYING;
        }
    }

    private void dispatchGameTransition(Consumer<MathBomberGameStats> transitionHandler) {
        if (flowState == SceneFlowState.TERMINAL) {
            return;
        }
        flowState = SceneFlowState.TERMINAL;
        if (transitionHandler != null) {
            transitionHandler.accept(buildCurrentStats());
        }
    }

    public MathBomberGameStats buildCurrentStats() {
        return new MathBomberGameStats(config.difficultyName, elapsedGameSeconds, roundState.getScore());
    }

}
