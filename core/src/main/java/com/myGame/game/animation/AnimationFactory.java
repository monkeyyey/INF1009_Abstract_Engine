package com.myGame.game.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.myGame.engine.animation.AnimationComponent;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class AnimationFactory {
    private final Texture runUpTexture;
    private final Texture runDownTexture;
    private final Texture runHorizontalTexture;
    private final Texture idleFrontTexture;
    private final Texture bombSheetTexture;
    private boolean disposed;

    public AnimationFactory() {
        this.runUpTexture = new Texture(Gdx.files.internal("animations/run_up.png"));
        this.runDownTexture = new Texture(Gdx.files.internal("animations/run_down.png"));
        this.runHorizontalTexture = new Texture(Gdx.files.internal("animations/run_horizontal.png"));
        this.idleFrontTexture = new Texture(Gdx.files.internal("animations/idle_front.png"));
        this.bombSheetTexture = new Texture(Gdx.files.internal("animations/bomb_spritesheet.png"));
    }

    public AnimationComponent<MathPlayerAnimationState> createPlayerAnimationComponent() {
        ensureNotDisposed();
        Map<MathPlayerAnimationState, Animation<TextureRegion>> clips = new EnumMap<>(MathPlayerAnimationState.class);
        clips.put(MathPlayerAnimationState.RUN_UP, toAnimationFromGrid(runUpTexture, 3, 3, 0, 0.11f, false));
        clips.put(MathPlayerAnimationState.RUN_DOWN, toAnimationFromGrid(runDownTexture, 3, 3, 0, 0.11f, false));
        clips.put(MathPlayerAnimationState.RUN_RIGHT, toAnimationFromGrid(runHorizontalTexture, 3, 3, 0, 0.11f, false));
        clips.put(MathPlayerAnimationState.RUN_LEFT, toAnimationFromGrid(runHorizontalTexture, 3, 3, 0, 0.11f, true));
        clips.put(MathPlayerAnimationState.IDLE_DOWN, toAnimationFromGrid(idleFrontTexture, 3, 2, 0, 0.18f, false));
        return new AnimationComponent<>(MathPlayerAnimationState.class, clips, MathPlayerAnimationState.IDLE_DOWN);
    }

    public AnimationComponent<MathBombAnimationState> createBombAnimationComponent() {
        ensureNotDisposed();
        int cols = 5;
        int rows = 1;
        int fuseFrames = 3;
        int frameWidth = bombSheetTexture.getWidth() / cols;
        int frameHeight = bombSheetTexture.getHeight() / rows;
        TextureRegion[][] grid = TextureRegion.split(bombSheetTexture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[fuseFrames];
        for (int i = 0; i < fuseFrames; i++) {
            frames[i] = new TextureRegion(grid[0][i]);
        }
        Map<MathBombAnimationState, Animation<TextureRegion>> clips = new EnumMap<>(MathBombAnimationState.class);
        clips.put(MathBombAnimationState.FUSE, new Animation<>(0.14f, frames));
        return new AnimationComponent<>(
                MathBombAnimationState.class,
                clips,
                Collections.singletonMap(MathBombAnimationState.FUSE, true),
                MathBombAnimationState.FUSE);
    }

    public void dispose() {
        if (disposed) return;
        disposed = true;
        runUpTexture.dispose();
        runDownTexture.dispose();
        runHorizontalTexture.dispose();
        idleFrontTexture.dispose();
        bombSheetTexture.dispose();
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
            if (flipX) {
                region.flip(true, false);
            }
            frames[i] = region;
        }
        return new Animation<>(frameDuration, frames);
    }

    private void ensureNotDisposed() {
        if (disposed) {
            throw new IllegalStateException("MathBomberAnimationFactory has been disposed.");
        }
    }
}
