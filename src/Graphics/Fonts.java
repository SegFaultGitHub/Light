package Graphics;

import org.jsfml.graphics.Font;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public abstract class Fonts {
    private static HashMap<String, Font> fonts;

    public static void initialize() throws IOException {
        fonts = new HashMap<>();
        Font calibri = new Font();
        calibri.loadFromFile(Paths.get("data/fonts/Calibri.ttf"));
        fonts.put("Calibri", calibri);
        Font monospace = new Font();
        monospace.loadFromFile(Paths.get("data/fonts/consola.ttf"));
        fonts.put("Monospace", monospace);
        Font monospaceBold = new Font();
        monospaceBold.loadFromFile(Paths.get("data/fonts/consolab.ttf"));
        fonts.put("MonospaceBold", monospaceBold);
        Font monospaceItalic = new Font();
        monospaceItalic.loadFromFile(Paths.get("data/fonts/consolai.ttf"));
        fonts.put("MonospaceItalic", monospaceItalic);
        Font monospaceBoldItalic = new Font();
        monospaceBoldItalic.loadFromFile(Paths.get("data/fonts/consolaz.ttf"));
        fonts.put("MonospaceBoldItalic", monospaceBoldItalic);
    }

    public static Font getFont(String name) {
        return fonts.get(name);
    }
}
