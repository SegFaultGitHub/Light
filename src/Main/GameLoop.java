package Main;

import Globals.Globals;
import Graphics.Fonts;
import Map.Map;
import MapEditor.Map.RawMap;
import Particles.Particles;
import Player.Player;
import Utils.Input;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.window.Keyboard;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;

import java.util.ArrayList;
import java.util.List;

public abstract class GameLoop {
    // Public
    public static RenderWindow window;
    public static long frame = 0;

    // Private
    private static final List<RenderTexture> gameLayers = new ArrayList<>();
    private static RenderTexture shadeLayer;
    private static final List<RenderTexture> interfaceLayers = new ArrayList<>();

    private static final Clock clock = new Clock();
    private static final Text fpsText = new Text();
    private static final List<Integer> fps = new ArrayList<>();

    // Temp
    private static Player player;
    private static Map map;
    private static final Text debugText = new Text();


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

        // FPS text setup
        fpsText.setPosition(10, 10);
        fpsText.setColor(Color.WHITE);
        fpsText.setFont(Fonts.getFont("Monospace"));
        fpsText.setCharacterSize(13);

        // Temp
        map = new Map(new RawMap("out/test.json"));
        player = new Player(map);
        map.placePlayer(player);
        debugText.setPosition(10,Globals.SCREEN_HEIGHT - 50);
        debugText.setColor(Color.WHITE);
        debugText.setFont(Fonts.getFont("Monospace"));
        debugText.setCharacterSize(13);
    }

    public static void update() {
        frame++;
        Input.update(window, window.pollEvents());
        if (Input.isKeyPressedOnce(Keyboard.Key.ESCAPE)) System.exit(0);

        // Compute FPS
        int now = (int) clock.getElapsedTime().asMicroseconds();
        fps.add(now);
        while (now - fps.get(0) > 1_000_000) fps.remove(0);
        fpsText.setString(String.valueOf(fps.size()));

        // Updates go here
        player.update(map);
        map.update(player);
        Particles.update(map);

        // Temp
    }

    public static void draw() {
        window.clear(Color.WHITE);

        gameLayers.forEach(layer -> layer.clear(Color.TRANSPARENT));
        shadeLayer.clear(Color.BLACK);
        interfaceLayers.forEach(layer -> layer.clear(Color.TRANSPARENT));

        player.draw(gameLayers, map.xDrawOffset, map.yDrawOffset);
        map.draw(gameLayers, shadeLayer);
        Particles.draw(gameLayers, 1, map.xDrawOffset, map.yDrawOffset);

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

        window.draw(fpsText);
        window.draw(debugText);
        window.display();
    }
}
