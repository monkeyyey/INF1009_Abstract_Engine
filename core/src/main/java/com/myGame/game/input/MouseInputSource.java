package com.myGame.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import com.myGame.engine.entities.Entity;

public class MouseInputSource implements InputSource {
    private static final float DEFAULT_DEADZONE = 10f;
    private final Entity player;
    private final float deadzone;

    public MouseInputSource(Entity player) {
        this(player, DEFAULT_DEADZONE);
    }

    public MouseInputSource(Entity player, float deadzone) {
        this.player = player;
        this.deadzone = Math.max(0f, deadzone);
    }

    @Override
    public void updateState(InputState state) {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        float dx = mouseX - player.getX();
        float dy = mouseY - player.getY();

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
