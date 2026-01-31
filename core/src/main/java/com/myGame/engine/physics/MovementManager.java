package com.myGame.engine.physics;

import com.myGame.engine.core.Movable;
import java.util.List;

public class MovementManager {
    private List<Movable> movables;

    public void setMovables(List<Movable> movables) {
        this.movables = movables;
    }

    public void update(float dt) {
        if (movables == null) return;
        for (Movable m : movables) {
            m.updatePosition(dt);
        }
    }
}
