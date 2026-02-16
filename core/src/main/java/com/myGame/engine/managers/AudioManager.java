package com.myGame.engine.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private final Map<String, Sound> soundMap = new HashMap<>();
    private final Map<String, Music> musicMap = new HashMap<>();
    private final Map<String, Float> musicVolumeMap = new HashMap<>();
    private String currentMusicName;

    public void loadSound(String name, String path) {
        Sound existing = soundMap.remove(name);
        if (existing != null) {
            existing.dispose();
        }
        soundMap.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void playSound(String name) {
        Sound sound = soundMap.get(name);
        if (sound != null) {
            sound.play();
        }
    }

    public void loadMusic(String name, String path) {
        Music existing = musicMap.remove(name);
        if (existing != null) {
            existing.dispose();
        }
        musicMap.put(name, Gdx.audio.newMusic(Gdx.files.internal(path)));
    }

    public void playMusic(String name) {
        Music target = musicMap.get(name);
        if (target == null) return;
        if (name.equals(currentMusicName) && target.isPlaying()) return;

        if (currentMusicName != null) {
            Music current = musicMap.get(currentMusicName);
            if (current != null) {
                current.stop();
            }
        }

        target.setLooping(true);
        target.setVolume(musicVolumeMap.getOrDefault(name, 1f));
        target.play();
        currentMusicName = name;
    }

    public void setMusicTrackVolume(String name, float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        musicVolumeMap.put(name, clamped);
        if (name.equals(currentMusicName)) {
            Music current = musicMap.get(currentMusicName);
            if (current != null) {
                current.setVolume(clamped);
            }
        }
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    public void setMusicVolume(float volume) {
        if (currentMusicName == null) return;
        Music current = musicMap.get(currentMusicName);
        if (current == null) return;
        float clamped = Math.max(0f, Math.min(1f, volume));
        current.setVolume(clamped);
    }

    public float getMusicVolume() {
        if (currentMusicName == null) return 0f;
        Music current = musicMap.get(currentMusicName);
        return current == null ? 0f : current.getVolume();
    }

    public void stopMusic() {
        if (currentMusicName == null) return;
        Music current = musicMap.get(currentMusicName);
        if (current != null) {
            current.stop();
        }
        currentMusicName = null;
    }

    public void dispose() {
        for (Sound s : soundMap.values()) {
            s.dispose();
        }
        for (Music m : musicMap.values()) {
            m.dispose();
        }
        soundMap.clear();
        musicMap.clear();
        currentMusicName = null;
    }
}
