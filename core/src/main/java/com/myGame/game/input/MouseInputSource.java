package com.myGame.game.input;

import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import com.myGame.engine.entities.Entity;

public class MouseInputSource implements InputSource {
    private Entity player;
    private final float DEADZONE = 10f;

    public MouseInputSource(Entity player) {
        this.player = player;
    }

    @Override
    public void updateState(InputState state) {
        // Poll mouse and update state
    }
}