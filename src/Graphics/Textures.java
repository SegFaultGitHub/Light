package Graphics;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

import java.nio.file.Paths;

/**
 * Created by Thomas VENNER on 21/07/2016.
 */
public abstract class Textures {
    public static Texture windowskin;
    public static Texture icons;

    public static final int ICON_SIZE = 24;

    public static void initialize() throws Exception {
        windowskin = new Texture();
        windowskin.loadFromFile(Paths.get("graphics/windowskin.png"));
        icons = new Texture();
        icons.loadFromFile(Paths.get("graphics/icons.png"));
    }

    public static Sprite getIcon(int x, int y) {
        Sprite icon = new Sprite();
        icon.setTexture(Textures.icons);
        icon.setTextureRect(new IntRect(x * ICON_SIZE, y * ICON_SIZE, ICON_SIZE, ICON_SIZE));
        return icon;
    }
}
