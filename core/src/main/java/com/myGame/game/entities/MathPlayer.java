package com.myGame.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.myGame.engine.animation.AnimationComponent;
import com.myGame.engine.collision.iCollidable;
import com.myGame.engine.collision.Hitboxes.CircleHitbox;
import com.myGame.engine.collision.Hitboxes.Hitbox;
import com.myGame.engine.entity.AnimatableEntity;
import com.myGame.engine.entity.Entity;
import com.myGame.engine.input.InputState;
import com.myGame.engine.movement.iMovable;
import com.myGame.game.animation.MathPlayerAnimationState;

import java.util.Objects;

public class MathPlayer extends AnimatableEntity<MathPlayerAnimationState> implements iMovable, iCollidable, iExplosionTarget {
    public interface OccupancyChecker {
        boolean canOccupy(float x, float y, float radius);
    }

    private int row;
    private int col;
    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private final float speed;

    private float velocityX;
    private float velocityY;
    private Hitbox hitbox;
    private OccupancyChecker occupancyChecker;
    private Runnable explosionHitHandler;

    public MathPlayer(int row,
                      int col,
                      float tileSize,
                      float boardX,
                      float boardY,
                      float speed,
                      AnimationComponent<MathPlayerAnimationState> animation) {
        this(row, col, tileSize, boardX, boardY, speed, animation, 1f);
    }

    public MathPlayer(int row,
                      int col,
                      float tileSize,
                      float boardX,
                      float boardY,
                      float speed,
                      AnimationComponent<MathPlayerAnimationState> animation,
                      float hitboxScaleMultiplier) {
        super(boardX + (col + 0.5f) * tileSize, boardY + (row + 0.5f) * tileSize, animation);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.speed = speed;
        this.hitbox = new CircleHitbox(getRadius() * hitboxScaleMultiplier);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.velocityX = 0f;
        this.velocityY = 0f;
        setX(worldX(col));
        setY(worldY(row));
    }

    public void setOccupancyChecker(OccupancyChecker occupancyChecker) {
        this.occupancyChecker = occupancyChecker;
    }

    public void setExplosionHitHandler(Runnable explosionHitHandler) {
        this.explosionHitHandler = explosionHitHandler;
    }

    public void applyInput(InputState input) {
        float dx = 0f;
        float dy = 0f;
        if (input.isLeft()) dx -= 1f;
        if (input.isRight()) dx += 1f;
        if (input.isUp()) dy += 1f;
        if (input.isDown()) dy -= 1f;

        if (dx == 0f && dy == 0f) {
            velocityX = 0f;
            velocityY = 0f;
            getAnimation().setState(MathPlayerAnimationState.IDLE_DOWN, false);
            return;
        }

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        velocityX = (dx / len) * speed;
        velocityY = (dy / len) * speed;
        updateFacingAndAnimation(dx, dy);
    }

    @Override
    public void updatePosition(float dt) {
        float radius = getRadius();

        float candidateX = getX() + velocityX * dt;
        if (occupancyChecker == null || occupancyChecker.canOccupy(candidateX, getY(), radius)) {
            setX(candidateX);
        }

        float candidateY = getY() + velocityY * dt;
        if (occupancyChecker == null || occupancyChecker.canOccupy(getX(), candidateY, radius)) {
            setY(candidateY);
        }

        syncGridFromWorld();
    }

    private void syncGridFromWorld() {
        this.col = Math.max(0, Math.round((getX() - boardX) / tileSize - 0.5f));
        this.row = Math.max(0, Math.round((getY() - boardY) / tileSize - 0.5f));
    }

    @Override
    public void onCollision(Entity other) {
        // Player collision responses are handled by the colliding entity.
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public void setHitbox(Hitbox hitbox) {
        Objects.requireNonNull(hitbox, "Hitbox cannot be null");
        if (!(hitbox instanceof CircleHitbox)) {
            throw new IllegalArgumentException("MathPlayer requires CircleHitbox");
        }
        this.hitbox = hitbox;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = getAnimation().getCurrentFrame();
        float drawSize = tileSize * 2.50f;
        float drawX = getX() - drawSize * 0.5f;
        float drawY = getY() - drawSize * 0.5f;
        batch.draw(frame, drawX, drawY, drawSize, drawSize);
    }

    @Override
    public void dispose() {
        // No owned resources.
    }

    @Override
    public TargetType getExplosionTargetType() {
        return TargetType.PLAYER;
    }

    @Override
    public void onExplosionHit() {
        if (explosionHitHandler != null) {
            explosionHitHandler.run();
        }
    }

    private float worldX(int col) {
        return boardX + (col + 0.5f) * tileSize;
    }

    private float worldY(int row) {
        return boardY + (row + 0.5f) * tileSize;
    }

    private float getRadius() {
        return tileSize * 0.33f;
    }

    private void updateFacingAndAnimation(float dx, float dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (dx > 0f) {
                getAnimation().setState(MathPlayerAnimationState.RUN_RIGHT);
            } else {
                getAnimation().setState(MathPlayerAnimationState.RUN_LEFT);
            }
        } else if (dy > 0f) {
            getAnimation().setState(MathPlayerAnimationState.RUN_UP);
        } else {
            getAnimation().setState(MathPlayerAnimationState.RUN_DOWN);
        }
    }
}
