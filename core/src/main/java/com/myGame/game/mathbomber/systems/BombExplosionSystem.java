package com.myGame.game.mathbomber.systems;

import com.myGame.game.entities.ExplosionCell;
import com.myGame.game.entities.MathBomb;
import com.myGame.game.factory.MathBomberFactory;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.round.ExplosionOutcome;
import com.myGame.game.mathbomber.round.RoundEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BombExplosionSystem {
    private final MathBomberFactory factory;
    private final MathBomberConfig config;
    private final Runnable onExplosionTriggered;
    private final List<MathBomb> expiredBombs = new ArrayList<>();

    public BombExplosionSystem(MathBomberFactory factory,
                               MathBomberConfig config,
                               Runnable onExplosionTriggered) {
        this.factory = factory;
        this.config = config;
        this.onExplosionTriggered = onExplosionTriggered;
    }

    public void updateBombs(Board board,
                            RoundEntities roundEntities,
                            ExplosionOutcome explosionOutcome,
                            Consumer<ExplosionCell> explosionRegistrar) {
        expiredBombs.clear();
        roundEntities.forEachBomb(bomb -> {
            if (bomb.isActive() && bomb.isExpired()) {
                expiredBombs.add(bomb);
            }
        });

        for (MathBomb bomb : expiredBombs) {
            onExplosionTriggered.run();
            explosionOutcome.markExplosionTriggered();
            List<Board.BlastCell> blastCells = board.computeBlastCells(
                    bomb.getRow(),
                    bomb.getCol(),
                    bomb.getBlastRange());

            for (Board.BlastCell cell : blastCells) {
                ExplosionCell explosion = factory.createExplosion(
                        board,
                        cell.getRow(),
                        cell.getCol(),
                        config.explosionDurationSeconds);
                roundEntities.addExplosion(explosion);
                explosionRegistrar.accept(explosion);
            }

            roundEntities.removeBomb(bomb);
        }
    }

    public void updateExplosions(RoundEntities roundEntities) {
        roundEntities.removeExpiredExplosions();
    }
}
