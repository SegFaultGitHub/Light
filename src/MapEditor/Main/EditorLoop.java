package MapEditor.Main;

import Globals.Globals;
import MapEditor.Map.EditorMap;
import Utils.Input;
import org.jsfml.graphics.*;
import org.jsfml.window.Keyboard;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class EditorLoop {
    // Public
    public static RenderWindow window;

    // Private
    private static final List<RenderTexture> gameLayers = new ArrayList<>();
    private static RenderTexture shadeLayer;
    private static final List<RenderTexture> interfaceLayers = new ArrayList<>();

    // Temp
    private static EditorMap map;


    // Public

    public static void initialize() throws Exception {
        window = new RenderWindow();
        window.create(new VideoMode((int) Globals.SCREEN_WIDTH, (int) Globals.SCREEN_HEIGHT, 32), "SFML-project", Globals.WINDOW_STYLE);
        window.setFramerateLimit(60);

        // Draw layers
        for (int i = 0; i < 10; i++) {
            gameLayers.add(getTexture());
            interfaceLayers.add(getTexture());
        }
        shadeLayer = getTexture();

        map = new EditorMap(10, 10, 50, 50);
    }

    public static void update() {
        Input.update(window, window.pollEvents());
        if (Input.isKeyPressedOnce(Keyboard.Key.ESCAPE)) System.exit(0);

        map.update();
    }

    public static void draw() {
        window.clear(Color.WHITE);

        gameLayers.forEach(layer -> layer.clear(Color.TRANSPARENT));
        interfaceLayers.forEach(layer -> layer.clear(Color.TRANSPARENT));

        map.draw(gameLayers, interfaceLayers, shadeLayer);

        drawLayers();
    }

    public static RenderTexture getTexture() throws TextureCreationException {
        RenderTexture texture = new RenderTexture();
        texture.create((int) Globals.SCREEN_WIDTH, (int) Globals.SCREEN_HEIGHT);
        return texture;
    }


    // Private

    private static void drawLayers() {
        for (RenderTexture layer : gameLayers) {
            layer.display();
            window.draw(new Sprite(layer.getTexture()));
        }
        shadeLayer.display();
        window.draw(new Sprite(shadeLayer.getTexture()), new RenderStates(BlendMode.MULTIPLY));
        for (RenderTexture layer : interfaceLayers) {
            layer.display();
            window.draw(new Sprite(layer.getTexture()));
        }
        window.display();
    }
}
