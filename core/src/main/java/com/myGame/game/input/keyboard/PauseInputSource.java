package com.myGame.game.input.keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.myGame.engine.input.InputSource;
import com.myGame.engine.input.InputState;

public class PauseInputSource implements InputSource {
    @Override
    public void updateState(InputState state) {
        state.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        state.setDown(Gdx.input.isKeyPressed(Input.Keys.DOWN));
        state.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        state.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        state.setPause(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE));
        state.setQuit(Gdx.input.isKeyJustPressed(Input.Keys.Q));
    }
}
