package com.ballplatformer;

import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public final Rectangle bounds;
    public float minX;
    public float maxX;
    public float speed;
    public float direction = 1f;
    public boolean alive = true;

    public Enemy(float x, float y, float width, float height, float minX, float maxX, float speed) {
        this.bounds = new Rectangle(x, y, width, height);
        this.minX = minX;
        this.maxX = maxX;
        this.speed = speed;
    }

    public void update(float delta) {
        bounds.x += direction * speed * delta;

        if (bounds.x <= minX) {
            bounds.x = minX;
            direction = 1f;
        }

        if (bounds.x + bounds.width >= maxX) {
            bounds.x = maxX - bounds.width;
            direction = -1f;
        }
    }
}
