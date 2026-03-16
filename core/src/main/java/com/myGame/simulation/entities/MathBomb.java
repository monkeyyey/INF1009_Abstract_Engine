package com.myGame.simulation.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.myGame.engine.Animation.AnimationComponent;
import com.myGame.engine.Animation.Interfaces.iAnimatable;
import com.myGame.engine.EntityManagement.AbstractEntities.Entity;
import com.myGame.engine.EntityManagement.Interfaces.iTickable;
import com.myGame.simulation.mathbomber.animation.MathBombAnimationState;
import java.util.Objects;

public class MathBomb extends Entity implements iTickable, iAnimatable {
    private final int row;
    private final int col;
    private final float tileSize;
    private final float fuseSeconds;
    private final int blastRange;
    private float timer;
    private final AnimationComponent<MathBombAnimationState> animation;

    public MathBomb(int row,
                    int col,
                    float tileSize,
                    float boardX,
                    float boardY,
                    float fuseSeconds,
                    int blastRange,
                    AnimationComponent<MathBombAnimationState> animation) {
        super(boardX + col * tileSize, boardY + row * tileSize);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.fuseSeconds = fuseSeconds;
        this.blastRange = blastRange;
        this.timer = fuseSeconds;
        this.animation = Objects.requireNonNull(animation, "Animation component cannot be null");
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
        // No owned resources.
    }
}
