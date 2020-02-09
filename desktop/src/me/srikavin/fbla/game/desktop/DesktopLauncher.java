package me.srikavin.fbla.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.srikavin.fbla.game.FBLAGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 1080;
        config.width = 1920;
        config.title = "The Quest for Success";
        config.addIcon("assets/icon/QS-128.png", Files.FileType.Internal);
        config.addIcon("assets/icon/QS-64.png", Files.FileType.Internal);
        config.addIcon("assets/icon/QS-32.png", Files.FileType.Internal);
        config.addIcon("assets/icon/QS-16.png", Files.FileType.Internal);
//		config.foregroundFPS = 1000;
//		config.vSyncEnabled = false;
//		config.fullscreen = true;
        new LwjglApplication(new FBLAGame(), config);
    }
}
