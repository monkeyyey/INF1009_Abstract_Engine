package com.myGame.simulation.input;

import com.badlogic.gdx.Gdx;
import com.myGame.engine.InputManagement.InputState;
import com.myGame.engine.InputManagement.Interfaces.InputSource;

public class MouseClickInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setPointerX(Gdx.input.getX());
        state.setPointerY(Gdx.graphics.getHeight() - Gdx.input.getY());
        state.setJustTouched(Gdx.input.justTouched());
    }
}
