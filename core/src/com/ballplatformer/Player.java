package com.ballplatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    public final Rectangle bounds;
    public final float radius = 15f;
    public float vx;
    public float vy;
    public float speed = 290f;
    public float jumpForce = 620f;
    public float gravity = 1500f;
    public boolean grounded = false;
    public int lives = 3;
    public int score = 0;

    public Player(float x, float y) {
        this.bounds = new Rectangle(x, y, 30f, 30f);
    }

    public void reset(float x, float y) {
        bounds.setPosition(x, y);
        vx = 0f;
        vy = 0f;
        grounded = false;
    }

    public void update(float delta, Level level) {
        float moveInput = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveInput -= 1f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveInput += 1f;
        }

        if (moveInput != 0f) {
            vx = moveInput * speed;
        } else {
            vx *= 0.82f;
            if (Math.abs(vx) < 5f) {
                vx = 0f;
            }
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) && grounded) {
            vy = jumpForce;
            grounded = false;
        }

        vy -= gravity * delta;

        bounds.x += vx * delta;
        resolveHorizontalCollisions(level);

        bounds.y += vy * delta;
        resolveVerticalCollisions(level);
    }

    private void resolveHorizontalCollisions(Level level) {
        for (Platform platform : level.platforms) {
            if (bounds.overlaps(platform.bounds)) {
                if (vx > 0f) {
                    bounds.x = platform.bounds.x - bounds.width;
                } else if (vx < 0f) {
                    bounds.x = platform.bounds.x + platform.bounds.width;
                }
                vx = 0f;
            }
        }
    }

    private void resolveVerticalCollisions(Level level) {
        grounded = false;

        for (Platform platform : level.platforms) {
            if (bounds.overlaps(platform.bounds)) {
                if (vy < 0f) {
                    bounds.y = platform.bounds.y + platform.bounds.height;
                    vy = 0f;
                    grounded = true;
                } else if (vy > 0f) {
                    bounds.y = platform.bounds.y - bounds.height;
                    vy = 0f;
                }
            }
        }
    }
}
