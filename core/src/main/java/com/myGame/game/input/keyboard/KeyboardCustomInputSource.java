package com.myGame.game.input.keyboard;

import com.badlogic.gdx.Gdx;
import com.myGame.engine.input.InputSource;
import com.myGame.engine.input.InputState;

public class KeyboardCustomInputSource implements InputSource {
    private static final int DISABLED_KEY = -1;

    private final int leftKey;
    private final int rightKey;
    private final int upKey;
    private final int downKey;
    private final int action1Key;
    private final int action2Key;
    private final int pauseKey;
    private final int quitKey;

    public KeyboardCustomInputSource(
            int leftKey,
            int rightKey,
            int upKey,
            int downKey,
            int action1Key) {
        this(leftKey, rightKey, upKey, downKey, action1Key, DISABLED_KEY, DISABLED_KEY, DISABLED_KEY);
    }

    public KeyboardCustomInputSource(
            int leftKey,
            int rightKey,
            int upKey,
            int downKey,
            int action1Key,
            int action2Key,
            int pauseKey) {
        this(leftKey, rightKey, upKey, downKey, action1Key, action2Key, pauseKey, DISABLED_KEY);
    }

    public KeyboardCustomInputSource(
            int leftKey,
            int rightKey,
            int upKey,
            int downKey,
            int action1Key,
            int action2Key,
            int pauseKey,
            int quitKey) {
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.downKey = downKey;
        this.action1Key = action1Key;
        this.action2Key = action2Key;
        this.pauseKey = pauseKey;
        this.quitKey = quitKey;
    }

    @Override
    public void updateState(InputState state) {
        applyIfEnabled(leftKey, state::setLeft);
        applyIfEnabled(rightKey, state::setRight);
        applyIfEnabled(upKey, state::setUp);
        applyIfEnabled(downKey, state::setDown);
        applyIfEnabled(action1Key, state::setAction1);
        applyIfEnabled(action2Key, state::setAction2);
        applyIfEnabled(pauseKey, state::setPause);
        applyIfEnabled(quitKey, state::setQuit);
    }

    private void applyIfEnabled(int key, java.util.function.Consumer<Boolean> setter) {
        if (key == DISABLED_KEY) {
            return;
        }
        setter.accept(Gdx.input.isKeyPressed(key));
    }
}
