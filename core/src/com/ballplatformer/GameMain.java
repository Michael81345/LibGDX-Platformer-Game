package com.ballplatformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;

public class GameMain extends ApplicationAdapter {
    private static final float WORLD_WIDTH = 1280f;
    private static final float WORLD_HEIGHT = 720f;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private final Array<Level> levels = new Array<>();
    private Level currentLevel;
    private int levelIndex = 0;
    private Player player;
    private boolean gameWon = false;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.2f);

        buildLevels();
        loadLevel(0);
    }

    private void buildLevels() {
        levels.add(createLevel1());
        levels.add(createLevel2());
        levels.add(createLevel3());
        levels.add(createLevel4());
    }

    private void loadLevel(int index) {
        levelIndex = index;
        currentLevel = levels.get(index);
        player = new Player(currentLevel.spawnX, currentLevel.spawnY);
        player.lives = 3;
        gameWon = false;
    }

    private void respawnPlayer() {
        player.lives--;

        if (player.lives <= 0) {
            player.lives = 3;
            levelIndex = 0;
            loadLevel(0);
            return;
        }

        player.reset(currentLevel.spawnX, currentLevel.spawnY);
    }

    @Override
    public void render() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);

        if (!gameWon) {
            update(delta);
        }

        draw();
    }

    private void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            loadLevel(levelIndex);
            return;
        }

        player.update(delta, currentLevel);

        for (Enemy enemy : currentLevel.enemies) {
            if (!enemy.alive) {
                continue;
            }

            enemy.update(delta);

            if (enemy.bounds.overlaps(player.bounds)) {
                if (player.vy < 0 && player.bounds.y > enemy.bounds.y + enemy.bounds.height * 0.5f) {
                    enemy.alive = false;
                    player.vy = 400f;
                    player.score += 50;
                } else {
                    respawnPlayer();
                }
            }
        }

        if (player.bounds.y < -200f) {
            respawnPlayer();
        }

        if (player.bounds.overlaps(currentLevel.goal)) {
            if (levelIndex < levels.size - 1) {
                loadLevel(levelIndex + 1);
            } else {
                gameWon = true;
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(0.08f, 0.1f, 0.18f, 1f);
        Gdx.gl.glClear(16384);

        float targetCameraX = MathUtils.clamp(
                player.bounds.x + player.bounds.width / 2f,
                WORLD_WIDTH / 2f,
                Math.max(WORLD_WIDTH / 2f, currentLevel.width - WORLD_WIDTH / 2f)
        );
        camera.position.x = targetCameraX;
        camera.position.y = WORLD_HEIGHT / 2f;
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(currentLevel.backgroundColor);
        shapeRenderer.rect(0, 0, currentLevel.width, currentLevel.height);

        for (Platform platform : currentLevel.platforms) {
            shapeRenderer.setColor(new Color(0.2f, 0.8f, 0.5f, 1f));
            shapeRenderer.rect(platform.bounds.x, platform.bounds.y, platform.bounds.width, platform.bounds.height);
        }

        for (Enemy enemy : currentLevel.enemies) {
            if (!enemy.alive) {
                continue;
            }
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemy.bounds.x, enemy.bounds.y, enemy.bounds.width, enemy.bounds.height);
        }

        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(currentLevel.goal.x, currentLevel.goal.y, currentLevel.goal.width, currentLevel.goal.height);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(player.bounds.x + player.bounds.width / 2f, player.bounds.y + player.bounds.height / 2f, player.radius);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Level: " + (levelIndex + 1) + " / " + levels.size, 20f, WORLD_HEIGHT - 20f);
        font.draw(batch, "Lives: " + player.lives, 20f, WORLD_HEIGHT - 50f);
        font.draw(batch, "Score: " + player.score, 20f, WORLD_HEIGHT - 80f);

        if (gameWon) {
            font.draw(batch, "YOU WON! Press R to replay", WORLD_WIDTH / 2f - 150f, WORLD_HEIGHT / 2f);
        }
        batch.end();
    }

    private Level createLevel1() {
        Level level = new Level("Level 1", 1700f, WORLD_HEIGHT, 80f, 100f, new Color(0.18f, 0.27f, 0.42f, 1f));

        level.platforms.add(new Platform(0f, 0f, 520f, 80f));
        level.platforms.add(new Platform(640f, 0f, 420f, 80f));
        level.platforms.add(new Platform(1180f, 0f, 520f, 80f));

        level.platforms.add(new Platform(220f, 150f, 180f, 18f));
        level.platforms.add(new Platform(500f, 260f, 200f, 18f));
        level.platforms.add(new Platform(840f, 180f, 220f, 18f));
        level.platforms.add(new Platform(1130f, 290f, 200f, 18f));

        level.enemies.add(new Enemy(350f, 80f, 28f, 28f, 200f, 470f, 100f));
        level.enemies.add(new Enemy(930f, 80f, 28f, 28f, 700f, 1040f, 120f));

        level.goal = new Rectangle(1570f, 80f, 36f, 110f);
        return level;
    }

    private Level createLevel2() {
        Level level = new Level("Level 2", 1750f, WORLD_HEIGHT, 80f, 100f, new Color(0.12f, 0.28f, 0.34f, 1f));

        level.platforms.add(new Platform(0f, 0f, 470f, 80f));
        level.platforms.add(new Platform(630f, 0f, 420f, 80f));
        level.platforms.add(new Platform(1100f, 0f, 650f, 80f));

        level.platforms.add(new Platform(180f, 140f, 160f, 18f));
        level.platforms.add(new Platform(450f, 220f, 160f, 18f));
        level.platforms.add(new Platform(720f, 140f, 170f, 18f));
        level.platforms.add(new Platform(980f, 260f, 180f, 18f));
        level.platforms.add(new Platform(1280f, 180f, 180f, 18f));

        level.enemies.add(new Enemy(280f, 80f, 28f, 28f, 130f, 430f, 120f));
        level.enemies.add(new Enemy(760f, 80f, 28f, 28f, 680f, 1030f, 130f));
        level.enemies.add(new Enemy(1320f, 80f, 28f, 28f, 1160f, 1520f, 140f));

        level.goal = new Rectangle(1635f, 80f, 36f, 110f);
        return level;
    }

    private Level createLevel3() {
        Level level = new Level("Level 3", 1900f, WORLD_HEIGHT, 80f, 100f, new Color(0.20f, 0.17f, 0.35f, 1f));

        level.platforms.add(new Platform(0f, 0f, 440f, 80f));
        level.platforms.add(new Platform(560f, 0f, 380f, 80f));
        level.platforms.add(new Platform(1040f, 0f, 350f, 80f));
        level.platforms.add(new Platform(1470f, 0f, 430f, 80f));

        level.platforms.add(new Platform(150f, 150f, 150f, 18f));
        level.platforms.add(new Platform(420f, 250f, 170f, 18f));
        level.platforms.add(new Platform(660f, 180f, 200f, 18f));
        level.platforms.add(new Platform(930f, 260f, 180f, 18f));
        level.platforms.add(new Platform(1200f, 190f, 170f, 18f));
        level.platforms.add(new Platform(1520f, 280f, 180f, 18f));

        level.enemies.add(new Enemy(220f, 80f, 28f, 28f, 100f, 400f, 110f));
        level.enemies.add(new Enemy(700f, 80f, 28f, 28f, 610f, 930f, 135f));
        level.enemies.add(new Enemy(1180f, 80f, 28f, 28f, 1060f, 1360f, 130f));
        level.enemies.add(new Enemy(1560f, 80f, 28f, 28f, 1490f, 1820f, 150f));

        level.goal = new Rectangle(1795f, 80f, 36f, 110f);
        return level;
    }

    private Level createLevel4() {
        Level level = new Level("Level 4", 2050f, WORLD_HEIGHT, 80f, 100f, new Color(0.38f, 0.18f, 0.18f, 1f));

        level.platforms.add(new Platform(0f, 0f, 420f, 80f));
        level.platforms.add(new Platform(520f, 0f, 330f, 80f));
        level.platforms.add(new Platform(980f, 0f, 260f, 80f));
        level.platforms.add(new Platform(1360f, 0f, 290f, 80f));
        level.platforms.add(new Platform(1750f, 0f, 300f, 80f));

        level.platforms.add(new Platform(140f, 150f, 150f, 18f));
        level.platforms.add(new Platform(390f, 250f, 180f, 18f));
        level.platforms.add(new Platform(700f, 180f, 170f, 18f));
        level.platforms.add(new Platform(1010f, 260f, 180f, 18f));
        level.platforms.add(new Platform(1290f, 180f, 180f, 18f));
        level.platforms.add(new Platform(1580f, 280f, 180f, 18f));
        level.platforms.add(new Platform(1850f, 180f, 150f, 18f));

        level.enemies.add(new Enemy(240f, 80f, 28f, 28f, 120f, 380f, 110f));
        level.enemies.add(new Enemy(610f, 80f, 28f, 28f, 560f, 820f, 135f));
        level.enemies.add(new Enemy(1060f, 80f, 28f, 28f, 1010f, 1200f, 160f));
        level.enemies.add(new Enemy(1400f, 80f, 28f, 28f, 1360f, 1640f, 170f));
        level.enemies.add(new Enemy(1790f, 80f, 28f, 28f, 1760f, 1980f, 180f));

        level.goal = new Rectangle(1940f, 80f, 36f, 110f);
        return level;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
