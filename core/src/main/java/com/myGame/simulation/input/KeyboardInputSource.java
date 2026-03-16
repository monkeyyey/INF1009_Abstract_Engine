package com.myGame.simulation.input;

import com.myGame.engine.InputManagement.InputState;
import com.myGame.engine.InputManagement.Interfaces.InputSource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class KeyboardInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D));
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S));
        state.setAction1(Gdx.input.isKeyPressed(Input.Keys.SPACE));
    }
}
