package Globals;

import Graphics.Fonts;
import Graphics.Textures;
import Locale.Locale;
import Particles.Particles;
import Utils.Input;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

public abstract class Globals {
    public static float SCREEN_WIDTH, SCREEN_HEIGHT;
    public static int WINDOW_STYLE;

    public static void initializeEditor() throws Exception {
        initialize();
        Globals.setScreenSize(800, 600);
        WINDOW_STYLE = WindowStyle.CLOSE;
    }

    public static void initializeGame() throws Exception {
        initialize();
        Particles.initialize();
        Globals.setScreenSize(800, 600);
//        Globals.setScreenSize(VideoMode.getDesktopMode().width, VideoMode.getDesktopMode().height);
        WINDOW_STYLE = WindowStyle.CLOSE;
    }

    public static void initialize() throws Exception {
        Input.initialize();
        Fonts.initialize();
        Textures.initialize();
        Locale.initialize();
    }

    public static void setScreenSize(float screenWidth, float screenHeight) {
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;
    }
}
