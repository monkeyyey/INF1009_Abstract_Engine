package com.myGame.engine.managers;

import com.myGame.engine.scenes.Scene;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneManager {
    private Deque<Scene> sceneStack = new ArrayDeque<>();

    public void setScene(Scene next) {
        if(!sceneStack.isEmpty()) sceneStack.pop().dispose();
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

    public void update(float dt) {
        if(getActiveScene() != null) getActiveScene().update(dt);
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
        if(getActiveScene() != null) getActiveScene().render(batch, shape);
    }

    public void dispose() {
        while(!sceneStack.isEmpty()) sceneStack.pop().dispose();
    }
}
