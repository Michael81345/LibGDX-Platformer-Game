package com.ballplatformer;

public class DesktopLauncher {
    public static void main(String[] args) {
        com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration config = new com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration();
        config.setTitle("Ball Platformer 4 Levels");
        config.setWindowedMode(1280, 720);
        config.useVsync(true);
        config.setForegroundFPS(60);

        new com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application(new GameMain(), config);
    }
}
