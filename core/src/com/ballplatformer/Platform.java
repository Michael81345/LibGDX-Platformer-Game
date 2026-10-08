package com.ballplatformer;

import com.badlogic.gdx.math.Rectangle;

public class Platform {
    public final Rectangle bounds;

    public Platform(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }
}
