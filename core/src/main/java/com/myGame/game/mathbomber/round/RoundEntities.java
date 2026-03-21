package com.myGame.game.mathbomber.round;

import com.myGame.game.entities.ExplosionCell;
import com.myGame.game.entities.MathBomb;
import com.myGame.game.entities.MathEnemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class RoundEntities {
    private final List<MathEnemy> enemies = new ArrayList<>();
    private final List<MathBomb> bombs = new ArrayList<>();
    private final List<ExplosionCell> explosions = new ArrayList<>();

    public void forEachEnemy(Consumer<MathEnemy> action) {
        for (MathEnemy enemy : enemies) {
            action.accept(enemy);
        }
    }

    public void forEachBomb(Consumer<MathBomb> action) {
        for (MathBomb bomb : bombs) {
            action.accept(bomb);
        }
    }

    public boolean hasActiveEnemyAt(int row, int col) {
        for (MathEnemy enemy : enemies) {
            if (!enemy.isActive()) continue;
            if (enemy.getRow() == row && enemy.getCol() == col) return true;
        }
        return false;
    }

    public boolean hasActiveEnemyAtExcept(int row, int col, MathEnemy excluded) {
        for (MathEnemy enemy : enemies) {
            if (enemy == excluded || !enemy.isActive()) continue;
            if (enemy.getRow() == row && enemy.getCol() == col) return true;
        }
        return false;
    }

    public void addEnemy(MathEnemy enemy) {
        enemies.add(enemy);
    }

    public void addBomb(MathBomb bomb) {
        bombs.add(bomb);
    }

    public void addExplosion(ExplosionCell cell) {
        explosions.add(cell);
    }

    public boolean hasBombAt(int row, int col) {
        for (MathBomb bomb : bombs) {
            if (!bomb.isActive()) continue;
            if (bomb.getRow() == row && bomb.getCol() == col) return true;
        }
        return false;
    }

    public void removeBomb(MathBomb bomb) {
        if (!bombs.contains(bomb)) return;
        bomb.destroy();
        bombs.remove(bomb);
    }

    public void removeExpiredExplosions() {
        Iterator<ExplosionCell> it = explosions.iterator();
        while (it.hasNext()) {
            ExplosionCell cell = it.next();
            if (!cell.isExpired()) continue;
            cell.destroy();
            it.remove();
        }
    }

    public void removeInactiveEnemies() {
        enemies.removeIf(enemy -> !enemy.isActive());
    }

    public void clear() {
        for (MathEnemy enemy : enemies) enemy.destroy();
        for (MathBomb bomb : bombs) bomb.destroy();
        for (ExplosionCell cell : explosions) cell.destroy();
        enemies.clear();
        bombs.clear();
        explosions.clear();
    }
}
