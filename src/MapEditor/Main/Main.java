package MapEditor.Main;

import Globals.Globals;

public abstract class Main {
    public static void main(String[] args) throws Exception {
        Globals.initializeEditor();
        EditorLoop.initialize();

        while (true) {
            EditorLoop.update();
            EditorLoop.draw();
        }
    }
}
