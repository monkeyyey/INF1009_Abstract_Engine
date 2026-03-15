    package com.myGame.simulation.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class AnimationComponent<S extends Enum<S>> {
    private final Map<S, Animation<TextureRegion>> clips;
    private final Map<S, Boolean> loopByState;
    private S state;
    private float stateTime;

    public AnimationComponent(Class<S> stateType, Map<S, Animation<TextureRegion>> clips, S initialState) {
        this(stateType, clips, Collections.emptyMap(), initialState);
    }

    public AnimationComponent(Class<S> stateType,
                              Map<S, Animation<TextureRegion>> clips,
                              Map<S, Boolean> loopByState,
                              S initialState) {
        if (clips == null || clips.isEmpty()) {
            throw new IllegalArgumentException("Animation clips cannot be null or empty.");
        }
        if (initialState == null || !clips.containsKey(initialState)) {
            throw new IllegalArgumentException("Initial state must exist in clips.");
        }
        this.clips = Collections.unmodifiableMap(new EnumMap<>(clips));
        this.loopByState = new EnumMap<>(stateType);
        this.loopByState.putAll(loopByState);
        this.state = initialState;
        this.stateTime = 0f;
    }

    public void setState(S nextState) {
        setState(nextState, true);
    }

    public void setState(S nextState, boolean resetTimeOnChange) {
        if (nextState == null || !clips.containsKey(nextState)) {
            throw new IllegalArgumentException("Unknown animation state: " + nextState);
        }
        if (nextState != state) {
            state = nextState;
            if (resetTimeOnChange) {
                stateTime = 0f;
            }
        }
    }

    public S getState() {
        return state;
    }

    public void update(float dt) {
        stateTime += Math.max(0f, dt);
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> animation = clips.get(state);
        return animation.getKeyFrame(stateTime, isLooping(state));
    }

    public boolean isCurrentAnimationFinished() {
        Animation<TextureRegion> animation = clips.get(state);
        return animation.isAnimationFinished(stateTime);
    }

    public void resetTime() {
        stateTime = 0f;
    }

    private boolean isLooping(S state) {
        return loopByState.getOrDefault(state, true);
    }
}
