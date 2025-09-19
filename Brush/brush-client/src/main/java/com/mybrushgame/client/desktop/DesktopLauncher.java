package com.mybrushgame.client.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Game;
import com.mybrushgame.client.ui.HomeScreenUI;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Brush Card Game");
        config.setWindowIcon("icon.png");
        config.setWindowedMode(1024, 768);

        // Launch a Game container that starts with HomeScreenUI
        new Lwjgl3Application(new Game() {
            @Override
            public void create() {
                setScreen(new HomeScreenUI(this));
            }
        }, config);
    }
}
