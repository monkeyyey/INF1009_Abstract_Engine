package com.myGame.simulation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;

public class KeyboardCustomInputSource implements InputSource {
    private static final int DISABLED_KEY = -1;

    private final int leftKey;
    private final int rightKey;
    private final int upKey;
    private final int downKey;
    private final int action1Key;
    private final int action2Key;
    private final int pauseKey;

    public KeyboardCustomInputSource(
            int leftKey,
            int rightKey,
            int upKey,
            int downKey,
            int action1Key) {
        this(leftKey, rightKey, upKey, downKey, action1Key, DISABLED_KEY, DISABLED_KEY);
    }

    public KeyboardCustomInputSource(
            int leftKey,
            int rightKey,
            int upKey,
            int downKey,
            int action1Key,
            int action2Key,
            int pauseKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.downKey = downKey;
        this.action1Key = action1Key;
        this.action2Key = action2Key;
        this.pauseKey = pauseKey;
    }

    @Override
    public void updateState(InputState state) {
        state.setLeft(isPressed(leftKey));
        state.setRight(isPressed(rightKey));
        state.setUp(isPressed(upKey));
        state.setDown(isPressed(downKey));
        state.setAction1(isPressed(action1Key));
        state.setAction2(isPressed(action2Key));
        state.setPause(isPressed(pauseKey));
    }

    private boolean isPressed(int key) {
        return key != DISABLED_KEY && Gdx.input.isKeyPressed(key);
    }
}
