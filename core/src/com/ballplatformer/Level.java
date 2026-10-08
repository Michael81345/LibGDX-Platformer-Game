package com.ballplatformer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Level {
    public final String name;
    public final float width;
    public final float height;
    public final float spawnX;
    public final float spawnY;
    public final Color backgroundColor;
    public final List<Platform> platforms = new ArrayList<>();
    public final List<Enemy> enemies = new ArrayList<>();
    public Rectangle goal;

    public Level(String name, float width, float height, float spawnX, float spawnY, Color backgroundColor) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.backgroundColor = backgroundColor;
    }
}
