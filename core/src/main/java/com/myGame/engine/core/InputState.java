package com.myGame.engine.core;

public class InputState {
    private boolean up, down, left, right;
    private boolean action1, action2, pause;

    public void copyFrom(InputState other) {
        this.up = other.up;
        this.down = other.down;
        this.left = other.left;
        this.right = other.right;
        this.action1 = other.action1;
        this.action2 = other.action2;
        this.pause = other.pause;
    }

    // Getters and Setters
    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }
    public boolean isDown() { return down; }
    public void setDown(boolean down) { this.down = down; }
    public boolean isLeft() { return left; }
    public void setLeft(boolean left) { this.left = left; }
    public boolean isRight() { return right; }
    public void setRight(boolean right) { this.right = right; }
    public boolean isAction1() { return action1; }
    public void setAction1(boolean action1) { this.action1 = action1; }
    public boolean isAction2() { return action2; }
    public void setAction2(boolean action2) { this.action2 = action2; }
    public boolean isPause() { return pause; }
    public void setPause(boolean pause) { this.pause = pause; }
}
