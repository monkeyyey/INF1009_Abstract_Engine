package com.myGame.game.input;

import com.badlogic.gdx.Gdx;
import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;

public class MouseClickInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setPointerX(Gdx.input.getX());
        state.setPointerY(Gdx.graphics.getHeight() - Gdx.input.getY());
        state.setJustTouched(Gdx.input.justTouched());
    }
}
