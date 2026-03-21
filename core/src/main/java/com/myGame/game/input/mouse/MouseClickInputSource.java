package com.myGame.game.input.mouse;

import com.badlogic.gdx.Gdx;
import com.myGame.engine.input.InputSource;
import com.myGame.engine.input.InputState;

public class MouseClickInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setPointerX(Gdx.input.getX());
        state.setPointerY(Gdx.graphics.getHeight() - Gdx.input.getY());
        state.setJustTouched(Gdx.input.justTouched());
    }
}
