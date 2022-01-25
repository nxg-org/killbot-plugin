package com.killbot.physics.util;

public class BubbleColumnDrag {
    public final float down = 0.03f;
    public final float maxDown;
    public final float up;
    public final float maxUp;

    public BubbleColumnDrag() {
        this(false);
    }

    public BubbleColumnDrag(boolean surface) {
        if (surface) {
            maxDown = -0.9f;
            up = 0.1f;
            maxUp = 1.8f;
        } else {
            maxDown = -0.3f;
            up = 0.06f;
            maxUp = 0.7f;
        }
    }
}