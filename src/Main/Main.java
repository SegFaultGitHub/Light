package Main;

import Globals.Globals;
import org.jsfml.window.VideoMode;

public abstract class Main {
    public static void main(String[] args) throws Exception {
        Globals.initializeGame();
        GameLoop.initialize();

        while (true) {
            GameLoop.update();
            GameLoop.draw();
        }
    }
}
