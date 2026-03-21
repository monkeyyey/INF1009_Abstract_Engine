package com.myGame.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.myGame.engine.animation.AnimationComponent;
import com.myGame.game.animation.MathBombAnimationState;
import com.myGame.engine.entity.AnimatableEntity;
import com.myGame.engine.lifecycle.iTickable;

public class MathBomb extends AnimatableEntity<MathBombAnimationState> implements iTickable {
    private final int row;
    private final int col;
    private final float tileSize;
    private final int blastRange;
    private float timer;

    public MathBomb(int row,
                    int col,
                    float tileSize,
                    float boardX,
                    float boardY,
                    float fuseSeconds,
                    int blastRange,
                    AnimationComponent<MathBombAnimationState> animation) {
        super(boardX + col * tileSize, boardY + row * tileSize, animation);
        this.row = row;
        this.col = col;
        this.tileSize = tileSize;
        this.blastRange = blastRange;
        this.timer = fuseSeconds;
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

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = getAnimation().getCurrentFrame();
        float drawSize = tileSize * 0.95f;
        float drawX = getX() + (tileSize - drawSize) * 0.5f;
        float drawY = getY() + (tileSize - drawSize) * 0.5f;
        batch.draw(frame, drawX, drawY, drawSize, drawSize);
    }

    @Override
    public void dispose() {
        // No owned resources.
    }
}
