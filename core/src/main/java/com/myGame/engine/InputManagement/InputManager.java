package com.myGame.engine.InputManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.myGame.engine.InputManagement.Interfaces.InputSource;
import com.myGame.engine.InputManagement.Interfaces.InputState;

public class InputManager {
    private final Map<Integer, InputSource> inputSources = new HashMap<>();
    private final Map<Integer, InputState> inputStates = new HashMap<>();

    public void addInputSource(int id, InputSource source) {
        Objects.requireNonNull(source, "InputSource cannot be null");
        inputSources.put(id, source);
        inputStates.put(id, new InputState());
    }

    public void removeInputSource(int id) {
        inputSources.remove(id);
        inputStates.remove(id);
    }

    public void update() {
        for (Map.Entry<Integer, InputSource> entry : inputSources.entrySet()) {
            InputState state = inputStates.get(entry.getKey());
            if (state == null) continue;
            state.beginFrameCapture();
            entry.getValue().updateState(state);
        }
    }

    public InputState getState(int id) {
        return inputStates.get(id);
    }
}
