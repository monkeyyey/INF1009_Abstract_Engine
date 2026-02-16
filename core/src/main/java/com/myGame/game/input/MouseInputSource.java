package com.myGame.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import com.myGame.engine.entities.Entity;

public class MouseInputSource implements InputSource {
    private static final float DEFAULT_DEADZONE = 10f;
    private final Entity target;
    private final float deadzone;

    public MouseInputSource(Entity target) {
        this(target, DEFAULT_DEADZONE);
    }

    public MouseInputSource(Entity target, float deadzone) {
        this.target = target;
        this.deadzone = Math.max(0f, deadzone);
    }

    @Override
    public void updateState(InputState state) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float dx = mouseX - target.getX();
        float dy = mouseY - target.getY();

        boolean moveHorizontally = Math.abs(dx) > deadzone;
        boolean moveVertically = Math.abs(dy) > deadzone;

        state.setLeft(moveHorizontally && dx < 0f);
        state.setRight(moveHorizontally && dx > 0f);
        state.setDown(moveVertically && dy < 0f);
        state.setUp(moveVertically && dy > 0f);
        state.setAction1(Gdx.input.isButtonPressed(Input.Buttons.LEFT));
        state.setAction2(Gdx.input.isButtonPressed(Input.Buttons.RIGHT));
        state.setPause(false);
    }
}
