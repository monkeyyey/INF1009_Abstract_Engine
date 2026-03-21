package com.myGame.game.mathbomber.systems;

import com.myGame.game.entities.MathEnemy;
import com.myGame.game.entities.MathPlayer;
import com.myGame.game.mathbomber.board.Board;
import com.myGame.game.mathbomber.round.RoundEntities;

import java.util.Objects;

public class OccupancyPolicy {
    private final Board board;
    private final RoundEntities roundEntities;
    private MathPlayer player;

    public OccupancyPolicy(Board board, RoundEntities roundEntities) {
        this.board = Objects.requireNonNull(board, "Board cannot be null");
        this.roundEntities = Objects.requireNonNull(roundEntities, "Round entities cannot be null");
    }

    public void setPlayer(MathPlayer player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null");
    }

    public boolean canOccupyPlayer(float candidateX, float candidateY, float radius) {
        ensurePlayerBound();
        Board.TileBounds candidateBounds = board.circleBoundsAt(candidateX, candidateY, radius);
        Board.TileBounds currentBounds = board.circleBoundsAt(player.getX(), player.getY(), radius);

        return board.allTilesPass(candidateBounds, (row, col) -> {
            if (!board.isInside(row, col)) return false;
            if (board.isWall(row, col)) return false;
            if (roundEntities.hasBombAt(row, col) && !board.tileBoundsContain(currentBounds, row, col)) return false;
            return true;
        });
    }

    public boolean canSpawnEnemyAt(int row, int col) {
        ensurePlayerBound();
        if (!board.isInside(row, col) || board.isWall(row, col)) return false;
        int manhattanDistance = Math.abs(player.getRow() - row) + Math.abs(player.getCol() - col);
        if (manhattanDistance < 3) return false;
        if (roundEntities.hasBombAt(row, col)) return false;
        return !roundEntities.hasActiveEnemyAt(row, col);
    }

    public boolean canEnemyMoveTo(MathEnemy enemy, int row, int col) {
        if (!board.isInside(row, col) || board.isWall(row, col)) return false;
        if (roundEntities.hasBombAt(row, col)) return false;
        return !roundEntities.hasActiveEnemyAtExcept(row, col, enemy);
    }

    private void ensurePlayerBound() {
        if (player == null) {
            throw new IllegalStateException("OccupancyPolicy requires player to be set before use");
        }
    }
}
