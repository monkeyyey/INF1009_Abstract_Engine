package com.myGame.game.input.keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.input.InputSource;
import com.myGame.engine.input.InputState;

public class KeyboardWASDInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.A));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.D));
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.W));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.S));
        state.setAction1(Gdx.input.isKeyPressed(Input.Keys.SPACE));
    }
}
