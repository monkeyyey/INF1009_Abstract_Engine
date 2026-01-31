package com.myGame.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;

public class KeyboardArrowInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.DOWN));
        state.setAction1(Gdx.input.isKeyPressed(Input.Keys.SPACE));
    }
}
