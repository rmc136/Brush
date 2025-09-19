package com.mybrushgame.client.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mybrushgame.client.game.BrushGameUI;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Brush Card Game");
        config.setWindowIcon("icon.png");
        config.setWindowedMode(1024, 768);
        new Lwjgl3Application(new BrushGameUI(), config);
    }
}
