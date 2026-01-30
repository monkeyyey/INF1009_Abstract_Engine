package com.myGame.engine.managers;

import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import java.util.HashMap;
import java.util.Map;

public class InputManager {
    private Map<Integer, InputSource> playerSources = new HashMap<>();
    private Map<Integer, InputState> playerInputs = new HashMap<>();

    public void addPlayer(int id, InputSource source) {
        playerSources.put(id, source);
        playerInputs.put(id, new InputState());
    }

    public void update() {
        for(Integer id : playerSources.keySet()) {
            playerSources.get(id).updateState(playerInputs.get(id));
        }
    }

    public InputState getState(int id) {
        return playerInputs.get(id);
    }
}
