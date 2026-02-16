package com.myGame.engine.managers;

import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import java.util.HashMap;
import java.util.Map;

public class InputManager {
    private final Map<Integer, InputSource> inputSources = new HashMap<>();
    private final Map<Integer, InputState> inputStates = new HashMap<>();

    public void addInputSource(int id, InputSource source) {
        inputSources.put(id, source);
        inputStates.put(id, new InputState());
    }

    public void removeInputSource(int id) {
        inputSources.remove(id);
        inputStates.remove(id);
    }

    public void update() {
        for(Integer id : inputSources.keySet()) {
            inputSources.get(id).updateState(inputStates.get(id));
        }
    }

    public InputState getState(int id) {
        return inputStates.get(id);
    }
}
