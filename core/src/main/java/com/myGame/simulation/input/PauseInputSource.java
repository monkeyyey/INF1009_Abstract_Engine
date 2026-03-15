package com.myGame.simulation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.InputManagement.Interfaces.InputSource;
import com.myGame.engine.InputManagement.Interfaces.InputState;

public class PauseInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.DOWN));
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        state.setAction1(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE));
        state.setAction2(Gdx.input.isKeyJustPressed(Input.Keys.Q));
        state.setPause(false);
    }
}
