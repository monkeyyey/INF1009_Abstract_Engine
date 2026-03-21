package com.myGame.game.factory.base;

import com.myGame.engine.animation.AnimationComponent;
import com.myGame.game.animation.MathBombAnimationState;
import com.myGame.game.animation.MathPlayerAnimationState;
import com.myGame.game.entities.ExplosionCell;
import com.myGame.game.entities.MathBomb;
import com.myGame.game.entities.MathPlayer;
import com.myGame.game.factory.MathBomberFactory;
import com.myGame.game.factory.support.EntityFactoryRegistry;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;

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
    public Board createBoard(float screenWidth, float screenHeight) {
        return new Board(config.rows, config.cols, config.tileSize, screenWidth, screenHeight);
    }

    @Override
    public MathPlayer createPlayer(Board board, AnimationComponent<MathPlayerAnimationState> animation) {
        return new MathPlayer(
                config.playerSpawnRow,
                config.playerSpawnCol,
                board.getTileSize(),
                board.getBoardX(),
                board.getBoardY(),
                config.playerSpeedUnitsPerSec,
                animation,
                config.playerHitboxScaleMultiplier);
    }

    @Override
    public MathBomb createBomb(Board board,
                               int row,
                               int col,
                               float fuseSeconds,
                               int blastRange,
                               AnimationComponent<MathBombAnimationState> animation) {
        BombSpawnRequest request = new BombSpawnRequest(board, row, col, fuseSeconds, blastRange, animation);
        return spawnFactoryRegistry.createEntity(MathBomb.class, request);
    }

    @Override
    public ExplosionCell createExplosion(Board board, int row, int col, float durationSeconds) {
        ExplosionSpawnRequest request = new ExplosionSpawnRequest(board, row, col, durationSeconds);
        return spawnFactoryRegistry.createEntity(ExplosionCell.class, request);
    }

    private void registerSpawnFactories() {
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

    private static final class BombSpawnRequest {
        private final Board board;
        private final int row;
        private final int col;
        private final float fuseSeconds;
        private final int blastRange;
        private final AnimationComponent<MathBombAnimationState> animation;

        private BombSpawnRequest(Board board,
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
        private final Board board;
        private final int row;
        private final int col;
        private final float durationSeconds;

        private ExplosionSpawnRequest(Board board, int row, int col, float durationSeconds) {
            this.board = board;
            this.row = row;
            this.col = col;
            this.durationSeconds = durationSeconds;
        }
    }
}
