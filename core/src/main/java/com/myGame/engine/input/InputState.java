package com.myGame.engine.input;

public class InputState {
    private boolean up, down, left, right;
    private boolean action1, action2, pause, quit;
    private boolean prevAction1, prevAction2;
    private float pointerX;
    private float pointerY;
    private boolean justTouched;

    public void beginFrameCapture() {
        this.prevAction1 = this.action1;
        this.prevAction2 = this.action2;
        this.up = false;
        this.down = false;
        this.left = false;
        this.right = false;
        this.action1 = false;
        this.action2 = false;
        this.pause = false;
        this.quit = false;
        this.pointerX = 0f;
        this.pointerY = 0f;
        this.justTouched = false;
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
    public boolean isAction1JustPressed() { return action1 && !prevAction1; }
    public void setAction1(boolean action1) { this.action1 = action1; }
    public boolean isAction2() { return action2; }
    public boolean isAction2JustPressed() { return action2 && !prevAction2; }
    public void setAction2(boolean action2) { this.action2 = action2; }
    public boolean isPause() { return pause; }
    public void setPause(boolean pause) { this.pause = pause; }
    public boolean isQuit() { return quit; }
    public void setQuit(boolean quit) { this.quit = quit; }
    public float getPointerX() { return pointerX; }
    public void setPointerX(float pointerX) { this.pointerX = pointerX; }
    public float getPointerY() { return pointerY; }
    public void setPointerY(float pointerY) { this.pointerY = pointerY; }
    public boolean isJustTouched() { return justTouched; }
    public void setJustTouched(boolean justTouched) { this.justTouched = justTouched; }
}
