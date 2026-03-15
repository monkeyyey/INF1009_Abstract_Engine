package com.myGame.simulation.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.myGame.engine.Animation.iAnimatable;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.EntityManagement.Interfaces.iTickable;
import com.myGame.simulation.animation.AnimationComponent;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MathBomb extends Entity implements iTickable, iAnimatable {
    private static final String BOMB_SHEET_PATH = "animations/bomb_spritesheet.png";
    private static final int SHEET_COLS = 5;
    private static final int SHEET_ROWS = 1;
    private static final int FUSE_FRAME_COUNT = 3;

    private static Texture sharedBombTexture;
    private static TextureRegion[] sharedBombFrames;
    private static Animation<TextureRegion> sharedFuseAnimation;
    private static int activeBombInstances;

    private final int row;
    private final int col;
    private final float tileSize;
    private final float fuseSeconds;
    private final int blastRange;
    private float timer;
    private final AnimationComponent<BombAnimationState> animation;

    private enum BombAnimationState {
        FUSE
    }

    public MathBomb(int row, int col, float tileSize, float boardX, float boardY, float fuseSeconds, int blastRange) {
        super(boardX + col * tileSize, boardY + row * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.fuseSeconds = fuseSeconds;
        this.blastRange = blastRange;
        this.timer = fuseSeconds;
        acquireSharedFrames();
        Map<BombAnimationState, Animation<TextureRegion>> clips = new EnumMap<>(BombAnimationState.class);
        clips.put(BombAnimationState.FUSE, sharedFuseAnimation);
        this.animation = new AnimationComponent<>(BombAnimationState.class, clips, Collections.singletonMap(BombAnimationState.FUSE, true), BombAnimationState.FUSE);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getBlastRange() {
        return blastRange;
    }

    @Override
    public void tick(float dt) {
        timer -= dt;
    }

    public boolean isExpired() {
        return timer <= 0f;
    }

    public float getFuseProgress01() {
        return Math.max(0f, Math.min(1f, 1f - (timer / fuseSeconds)));
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = animation.getCurrentFrame();
        float drawSize = tileSize * 0.95f;
        float drawX = getX() + (tileSize - drawSize) * 0.5f;
        float drawY = getY() + (tileSize - drawSize) * 0.5f;
        batch.draw(frame, drawX, drawY, drawSize, drawSize);
    }

    @Override
    public void updateAnimation(float dt) {
        animation.update(dt);
    }

    @Override
    public void dispose() {
        releaseSharedFrames();
    }

    private static void acquireSharedFrames() {
        if (sharedBombTexture == null) {
            sharedBombTexture = new Texture(Gdx.files.internal(BOMB_SHEET_PATH));
            int frameWidth = sharedBombTexture.getWidth() / SHEET_COLS;
            int frameHeight = sharedBombTexture.getHeight() / SHEET_ROWS;
            TextureRegion[][] grid = TextureRegion.split(sharedBombTexture, frameWidth, frameHeight);
            sharedBombFrames = new TextureRegion[SHEET_COLS];
            for (int i = 0; i < SHEET_COLS; i++) {
                sharedBombFrames[i] = new TextureRegion(grid[0][i]);
            }
            TextureRegion[] fuseFrames = new TextureRegion[FUSE_FRAME_COUNT];
            for (int i = 0; i < FUSE_FRAME_COUNT; i++) {
                fuseFrames[i] = new TextureRegion(sharedBombFrames[i]);
            }
            sharedFuseAnimation = new Animation<>(0.14f, fuseFrames);
        }
        activeBombInstances++;
    }

    private static void releaseSharedFrames() {
        activeBombInstances = Math.max(0, activeBombInstances - 1);
        if (activeBombInstances == 0 && sharedBombTexture != null) {
            sharedBombTexture.dispose();
            sharedBombTexture = null;
            sharedBombFrames = null;
            sharedFuseAnimation = null;
        }
    }
}
