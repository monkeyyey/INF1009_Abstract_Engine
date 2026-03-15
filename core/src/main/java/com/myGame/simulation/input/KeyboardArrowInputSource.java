package com.myGame.simulation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.InputManagement.Interfaces.InputSource;
import com.myGame.engine.InputManagement.Interfaces.InputState;

public class KeyboardArrowInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.DOWN));
        state.setAction1(Gdx.input.isKeyPressed(Input.Keys.SPACE));
        state.setAction2(Gdx.input.isKeyPressed(Input.Keys.R));
        state.setQuit(Gdx.input.isKeyPressed(Input.Keys.Q));
    }
}
