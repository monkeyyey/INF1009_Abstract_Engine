package com.myGame.engine.managers;

import com.myGame.engine.scenes.Scene;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneManager {
    private Deque<Scene> sceneStack = new ArrayDeque<>();
    private Set<Scene> detachedScenes = new HashSet<>();
    private List<Scene> cycleScenes = new ArrayList<>();

    public void setScene(Scene next) {
        if (!sceneStack.isEmpty()) {
            Scene current = sceneStack.pop();
            current.onExit();
            if (current != next) {
                detachedScenes.add(current);
            }
        }
        sceneStack.push(next);
        next.onEnter();
    }

    public void pushScene(Scene next) {
        sceneStack.push(next);
        next.onEnter();
    }

    public Scene popScene() {
        Scene s = sceneStack.pop();
        s.onExit();
        return s;
    }

    public Scene getActiveScene() {
        return sceneStack.peek();
    }

    public void registerCycleScene(Scene scene) {
        if (scene != null && !cycleScenes.contains(scene)) {
            cycleScenes.add(scene);
        }
    }

    public void cycleScene() {
        if (cycleScenes.isEmpty()) return;
        Scene active = getActiveScene();
        int currentIndex = cycleScenes.indexOf(active);
        int nextIndex = currentIndex < 0 ? 0 : (currentIndex + 1) % cycleScenes.size();
        setScene(cycleScenes.get(nextIndex));
    }

    public void update(float dt) {
        if(getActiveScene() != null) getActiveScene().update(dt);
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
        if(getActiveScene() != null) getActiveScene().render(batch, shape);
    }

    public void dispose() {
        Set<Scene> allScenes = new HashSet<>(detachedScenes);
        allScenes.addAll(sceneStack);
        for (Scene scene : allScenes) {
            scene.dispose();
        }
        sceneStack.clear();
        detachedScenes.clear();
    }
}
