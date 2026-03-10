package com.myGame.simulation.mathbomber.round;

import com.myGame.engine.managers.EntityManager;
import com.myGame.simulation.entities.ExplosionCell;
import com.myGame.simulation.entities.MathBomb;
import com.myGame.simulation.entities.MathEnemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MathRoundEntities {
    private final EntityManager entityManager;
    private final List<MathEnemy> enemies = new ArrayList<>();
    private final List<MathBomb> bombs = new ArrayList<>();
    private final List<ExplosionCell> explosions = new ArrayList<>();

    public MathRoundEntities(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<MathEnemy> enemySnapshot() {
        return new ArrayList<>(enemies);
    }

    public void forEachEnemy(Consumer<MathEnemy> action) {
        for (MathEnemy enemy : enemies) {
            action.accept(enemy);
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

    public List<MathBomb> bombSnapshot() {
        return new ArrayList<>(bombs);
    }

    public void addEnemy(String id, MathEnemy enemy) {
        enemies.add(enemy);
        entityManager.addEntity(id, enemy);
    }

    public void addBomb(String id, MathBomb bomb) {
        bombs.add(bomb);
        entityManager.addEntity(id, bomb);
    }

    public void addExplosion(String id, ExplosionCell cell) {
        explosions.add(cell);
        entityManager.addEntity(id, cell);
    }

    public boolean hasBombAt(int row, int col) {
        for (MathBomb bomb : bombs) {
            if (!bomb.isActive()) continue;
            if (bomb.getRow() == row && bomb.getCol() == col) return true;
        }
        return false;
    }

    public boolean containsBomb(MathBomb bomb) {
        return bombs.contains(bomb);
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

    public int bombsRemaining() {
        int alive = 0;
        for (MathBomb bomb : bombs) {
            if (bomb.isActive()) alive++;
        }
        return alive;
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
