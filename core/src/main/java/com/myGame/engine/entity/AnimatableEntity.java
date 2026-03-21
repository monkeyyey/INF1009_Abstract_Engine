package com.myGame.engine.entity;

import java.util.Objects;

import com.myGame.engine.animation.AnimationComponent;
import com.myGame.engine.animation.iAnimatable;

public abstract class AnimatableEntity<S extends Enum<S>> extends Entity implements iAnimatable {
    private final AnimationComponent<S> animation;

    protected AnimatableEntity(float x, float y, AnimationComponent<S> animation) {
        super(x, y);
        this.animation = Objects.requireNonNull(animation, "Animation component cannot be null");
    }

    protected final AnimationComponent<S> getAnimation() {
        return animation;
    }

    @Override
    public void updateAnimation(float dt) {
        animation.update(dt);
    }
}
