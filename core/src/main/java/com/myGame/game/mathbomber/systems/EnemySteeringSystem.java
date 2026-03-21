package com.myGame.game.mathbomber.systems;

import com.myGame.game.entities.MathEnemy;
import com.myGame.game.mathbomber.configurations.MathBomberConfig;
import com.myGame.game.mathbomber.round.RoundEntities;

public class EnemySteeringSystem {
    private static final int[][] FALLBACK_DIRECTIONS = {
            {0, 1},
            {0, -1},
            {1, 0},
            {-1, 0}
    };

    private final float moveDurationSeconds;
    private final OccupancyPolicy occupancyPolicy;

    public EnemySteeringSystem(MathBomberConfig config, OccupancyPolicy occupancyPolicy) {
        this.moveDurationSeconds = config.enemyMoveDurationSeconds;
        this.occupancyPolicy = occupancyPolicy;
    }

    public void update(RoundEntities roundEntities) {
        roundEntities.forEachEnemy(this::updateEnemyIntent);
    }

    private void updateEnemyIntent(MathEnemy enemy) {
        if (!enemy.isActive() || enemy.isMoving()) {
            return;
        }

        int currentDr = enemy.getLastDirRow();
        int currentDc = enemy.getLastDirCol();
        boolean moved = tryMove(enemy, currentDr, currentDc);
        if (moved) {
            return;
        }

        for (int[] dir : FALLBACK_DIRECTIONS) {
            if (dir[0] == currentDr && dir[1] == currentDc) {
                continue;
            }
            if (tryMove(enemy, dir[0], dir[1])) {
                return;
            }
        }

        enemy.setLastDirection(-currentDr, -currentDc);
    }

    private boolean tryMove(MathEnemy enemy,
                            int dirRow,
                            int dirCol) {
        if (dirRow == 0 && dirCol == 0) {
            return false;
        }
        int nextRow = enemy.getRow() + dirRow;
        int nextCol = enemy.getCol() + dirCol;
        if (!occupancyPolicy.canEnemyMoveTo(enemy, nextRow, nextCol)) {
            return false;
        }
        enemy.setLastDirection(dirRow, dirCol);
        enemy.startMoveTo(nextRow, nextCol, moveDurationSeconds);
        return true;
    }
}
