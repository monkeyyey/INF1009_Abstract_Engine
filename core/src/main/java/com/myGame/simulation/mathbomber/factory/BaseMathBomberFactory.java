package com.myGame.simulation.mathbomber.factory;

import com.myGame.engine.Animation.AnimationComponent;
import com.myGame.simulation.entities.ExplosionCell;
import com.myGame.simulation.entities.MathBomb;
import com.myGame.simulation.entities.MathEnemy;
import com.myGame.simulation.entities.MathPlayer;
import com.myGame.simulation.mathbomber.animation.MathBombAnimationState;
import com.myGame.simulation.mathbomber.animation.MathPlayerAnimationState;
import com.myGame.simulation.mathbomber.board.MathBoard;
import com.myGame.simulation.mathbomber.config.MathBomberConfig;

public abstract class BaseMathBomberFactory implements MathBomberFactory {
    private final MathBomberConfig config;
    private final EntityFactoryRegistry spawnFactoryRegistry = new EntityFactoryRegistry();

    protected BaseMathBomberFactory(MathBomberConfig config) {
        this.config = config;
        registerSpawnFactories();
    }

    protected MathBomberConfig config() {
        return config;
    }

    @Override
    public MathBoard createBoard(float screenWidth, float screenHeight) {
        return new MathBoard(config.rows, config.cols, config.tileSize, screenWidth, screenHeight);
    }

    @Override
    public MathPlayer createPlayer(MathBoard board, AnimationComponent<MathPlayerAnimationState> animation) {
        return new MathPlayer(
                config.playerSpawnRow,
                config.playerSpawnCol,
                board.getTileSize(),
                board.getBoardX(),
                board.getBoardY(),
                config.playerSpeedUnitsPerSec,
                config.playerColor,
                animation);
    }

    @Override
    public MathEnemy createEnemy(MathBoard board, int row, int col, int value) {
        EnemySpawnRequest request = new EnemySpawnRequest(board, row, col, value);
        return spawnFactoryRegistry.createEntity(MathEnemy.class, request);
    }

    @Override
    public MathBomb createBomb(MathBoard board,
                               int row,
                               int col,
                               float fuseSeconds,
                               int blastRange,
                               AnimationComponent<MathBombAnimationState> animation) {
        BombSpawnRequest request = new BombSpawnRequest(board, row, col, fuseSeconds, blastRange, animation);
        return spawnFactoryRegistry.createEntity(MathBomb.class, request);
    }

    @Override
    public ExplosionCell createExplosion(MathBoard board, int row, int col, float durationSeconds) {
        ExplosionSpawnRequest request = new ExplosionSpawnRequest(board, row, col, durationSeconds);
        return spawnFactoryRegistry.createEntity(ExplosionCell.class, request);
    }

    private void registerSpawnFactories() {
        spawnFactoryRegistry.registerFactory(MathEnemy.class, (EnemySpawnRequest req) -> new MathEnemy(
                req.row,
                req.col,
                req.value,
                req.board.getTileSize(),
                req.board.getBoardX(),
                req.board.getBoardY()));
        spawnFactoryRegistry.registerFactory(MathBomb.class, (BombSpawnRequest req) -> new MathBomb(
                req.row,
                req.col,
                req.board.getTileSize(),
                req.board.getBoardX(),
                req.board.getBoardY(),
                req.fuseSeconds,
                req.blastRange,
                req.animation));
        spawnFactoryRegistry.registerFactory(ExplosionCell.class, (ExplosionSpawnRequest req) -> new ExplosionCell(
                req.row,
                req.col,
                req.board.getTileSize(),
                req.board.getBoardX(),
                req.board.getBoardY(),
                req.durationSeconds));
    }

    private static final class EnemySpawnRequest {
        private final MathBoard board;
        private final int row;
        private final int col;
        private final int value;

        private EnemySpawnRequest(MathBoard board, int row, int col, int value) {
            this.board = board;
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

    private static final class BombSpawnRequest {
        private final MathBoard board;
        private final int row;
        private final int col;
        private final float fuseSeconds;
        private final int blastRange;
        private final AnimationComponent<MathBombAnimationState> animation;

        private BombSpawnRequest(MathBoard board,
                                 int row,
                                 int col,
                                 float fuseSeconds,
                                 int blastRange,
                                 AnimationComponent<MathBombAnimationState> animation) {
            this.board = board;
            this.row = row;
            this.col = col;
            this.fuseSeconds = fuseSeconds;
            this.blastRange = blastRange;
            this.animation = animation;
        }
    }

    private static final class ExplosionSpawnRequest {
        private final MathBoard board;
        private final int row;
        private final int col;
        private final float durationSeconds;

        private ExplosionSpawnRequest(MathBoard board, int row, int col, float durationSeconds) {
            this.board = board;
            this.row = row;
            this.col = col;
            this.durationSeconds = durationSeconds;
        }
    }
}
