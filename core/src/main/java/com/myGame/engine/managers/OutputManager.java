package com.myGame.engine.managers;

import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class OutputManager {
    private final Map<String, Sound> soundMap = new HashMap<>();

    public void loadSound(String name, String path) {
        // Gdx.audio.newSound implementation
    }

    public void playSound(String name) {
        if(soundMap.containsKey(name)) {
            soundMap.get(name).play();
        }
    }

    public void dispose() {
        for(Sound s : soundMap.values()) {
            s.dispose();
        }
    }
}
