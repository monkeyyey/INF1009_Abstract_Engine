package com.myGame.game.input;

import com.myGame.engine.core.InputSource;
import com.myGame.engine.core.InputState;
import java.util.HashMap;
import java.util.Map;

public class KeyboardInputSource implements InputSource {
    private Map<Integer, String> keyMappings;

    public KeyboardInputSource() {
        keyMappings = new HashMap<>();
        // Add default mappings
    }

    @Override
    public void updateState(InputState state) {
        // Poll keys and update state
    }
}
