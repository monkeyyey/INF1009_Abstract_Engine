package com.myGame.simulation.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.myGame.engine.Animation.iAnimatable;
import com.myGame.engine.Collision.Hitboxes.CircleHitbox;
import com.myGame.engine.Collision.Hitboxes.Hitbox;
import com.myGame.engine.Collision.Interfaces.iCollidable;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.InputManagement.Interfaces.InputState;
import com.myGame.engine.Movement.Interfaces.iMovable;
import com.myGame.simulation.animation.AnimationComponent;
import com.myGame.simulation.interfaces.ExplosionTarget;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class MathPlayer extends Entity implements iMovable, iCollidable, ExplosionTarget, iAnimatable {
    public interface OccupancyChecker {
        boolean canOccupy(float x, float y, float radius);
    }

    private int row;
    private int col;
    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private final float speed;
    private final Color color;

    private float velocityX;
    private float velocityY;
    private Hitbox hitbox;
    private OccupancyChecker occupancyChecker;
    private Runnable explosionHitHandler;

    private Texture runUpTexture;
    private Texture runDownTexture;
    private Texture runHorizontalTexture;
    private Texture idleFrontTexture;
    private final AnimationComponent<PlayerAnimationState> animation;

    private enum PlayerAnimationState {
        IDLE_DOWN,
        RUN_UP,
        RUN_DOWN,
        RUN_LEFT,
        RUN_RIGHT
    }

    public MathPlayer(int row, int col, float tileSize, float boardX, float boardY, float speed, Color color) {
        super(boardX + (col + 0.5f) * tileSize, boardY + (row + 0.5f) * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.boardX = boardX;
        this.boardY = boardY;
        this.speed = speed;
        this.color = color;
        this.hitbox = new CircleHitbox(getRadius());
        this.animation = initAnimationComponent();
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
            animation.setState(PlayerAnimationState.IDLE_DOWN, false);
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
        TextureRegion frame = animation.getCurrentFrame();
        float drawSize = tileSize * 2.50f;
        float drawX = getX() - drawSize * 0.5f;
        float drawY = getY() - drawSize * 0.5f;
        batch.draw(frame, drawX, drawY, drawSize, drawSize);
    }

    @Override
    public void updateAnimation(float dt) {
        animation.update(dt);
    }

    @Override
    public void dispose() {
        if (runUpTexture != null) runUpTexture.dispose();
        if (runDownTexture != null) runDownTexture.dispose();
        if (runHorizontalTexture != null) runHorizontalTexture.dispose();
        if (idleFrontTexture != null) idleFrontTexture.dispose();
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
                animation.setState(PlayerAnimationState.RUN_RIGHT);
            } else {
                animation.setState(PlayerAnimationState.RUN_LEFT);
            }
        } else if (dy > 0f) {
            animation.setState(PlayerAnimationState.RUN_UP);
        } else {
            animation.setState(PlayerAnimationState.RUN_DOWN);
        }
    }

    private AnimationComponent<PlayerAnimationState> initAnimationComponent() {
        runUpTexture = new Texture(Gdx.files.internal("animations/run_up.png"));
        runDownTexture = new Texture(Gdx.files.internal("animations/run_down.png"));
        runHorizontalTexture = new Texture(Gdx.files.internal("animations/run_horizontal.png"));
        idleFrontTexture = new Texture(Gdx.files.internal("animations/idle_front.png"));

        Map<PlayerAnimationState, Animation<TextureRegion>> clips = new EnumMap<>(PlayerAnimationState.class);
        clips.put(PlayerAnimationState.RUN_UP, toAnimationFromGrid(runUpTexture, 3, 3, 0, 0.11f, false));
        clips.put(PlayerAnimationState.RUN_DOWN, toAnimationFromGrid(runDownTexture, 3, 3, 0, 0.11f, false));
        clips.put(PlayerAnimationState.RUN_RIGHT, toAnimationFromGrid(runHorizontalTexture, 3, 3, 0, 0.11f, false));
        clips.put(PlayerAnimationState.RUN_LEFT, toAnimationFromGrid(runHorizontalTexture, 3, 3, 0, 0.11f, true));
        clips.put(PlayerAnimationState.IDLE_DOWN, toAnimationFromGrid(idleFrontTexture, 3, 2, 0, 0.18f, false));
        return new AnimationComponent<>(PlayerAnimationState.class, clips, PlayerAnimationState.IDLE_DOWN);
    }

    private Animation<TextureRegion> toAnimationFromGrid(Texture texture,
                                                         int cols,
                                                         int rows,
                                                         int rowIndex,
                                                         float frameDuration,
                                                         boolean flipX) {
        int frameWidth = Math.max(1, texture.getWidth() / Math.max(1, cols));
        int frameHeight = Math.max(1, texture.getHeight() / Math.max(1, rows));
        TextureRegion[][] split = TextureRegion.split(texture, frameWidth, frameHeight);
        int selectedRow = Math.max(0, Math.min(rowIndex, split.length - 1));
        TextureRegion[] frames = new TextureRegion[split[selectedRow].length];
        for (int i = 0; i < split[selectedRow].length; i++) {
            TextureRegion region = new TextureRegion(split[selectedRow][i]);
            if (flipX) region.flip(true, false);
            frames[i] = region;
        }
        return new Animation<>(frameDuration, frames);
    }
}
