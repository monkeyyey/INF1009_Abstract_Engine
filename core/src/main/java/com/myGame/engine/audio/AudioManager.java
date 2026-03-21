package com.myGame.engine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AudioManager {
    private final Map<String, Sound> soundMap = new HashMap<>();
    private final Map<String, Music> musicMap = new HashMap<>();
    private String currentMusicName;
    private float sharedMusicVolume = 1f;
    private float sharedSfxVolume = 1f;

    public void loadSound(String name, String path) {
        validateAssetKeyAndPath(name, path);
        Sound existing = soundMap.remove(name);
        if (existing != null) {
            existing.dispose();
        }
        soundMap.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
    }

    public void playSound(String name) {
        validateKey(name, "Sound name");
        Sound sound = soundMap.get(name);
        if (sound == null) {
            throw new IllegalArgumentException("Sound not loaded: " + name);
        }
        sound.play(sharedSfxVolume);
    }

    public void loadMusic(String name, String path) {
        validateAssetKeyAndPath(name, path);
        Music existing = musicMap.remove(name);
        if (existing != null) {
            existing.dispose();
        }
        Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setVolume(sharedMusicVolume);
        musicMap.put(name, music);
    }

    public void playMusic(String name) {
        validateKey(name, "Music name");
        Music target = musicMap.get(name);
        if (target == null) {
            throw new IllegalArgumentException("Music not loaded: " + name);
        }
        if (name.equals(currentMusicName) && target.isPlaying()) return;

        if (currentMusicName != null) {
            Music current = musicMap.get(currentMusicName);
            if (current != null) {
                current.stop();
            }
        }

        target.setLooping(true);
        target.setVolume(sharedMusicVolume);
        target.play();
        currentMusicName = name;
    }

    public String getCurrentMusicName() {
        return currentMusicName;
    }

    public void setMusicVolume(float volume) {
        float clamped = Math.max(0f, Math.min(1f, volume));
        sharedMusicVolume = clamped;
        for (Music music : musicMap.values()) {
            if (music != null) {
                music.setVolume(clamped);
            }
        }
    }

    public float getMusicVolume() {
        if (currentMusicName != null) {
            Music current = musicMap.get(currentMusicName);
            if (current != null) {
                return current.getVolume();
            }
        }
        return sharedMusicVolume;
    }

    public void setSfxVolume(float volume) {
        sharedSfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public float getSfxVolume() {
        return sharedSfxVolume;
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

    private static void validateAssetKeyAndPath(String name, String path) {
        validateKey(name, "Asset name");
        validateKey(path, "Asset path");
    }

    private static void validateKey(String value, String label) {
        Objects.requireNonNull(value, label + " cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be blank");
        }
    }
}
