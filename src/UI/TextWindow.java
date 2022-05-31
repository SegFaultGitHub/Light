package UI;

import Graphics.Fonts;
import Utils.Utils;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas VENNER on 23/07/2016.
 */
public class TextWindow extends Window {
    public static class TextWithStyle {
        public Color color;
        public String style;
        public String content;

        public TextWithStyle(Color color, String style, String content) {
            this.color = color;
            this.style = style;
            this.content = content;
        }
    }

    protected List<Text> texts;
    protected static final int TEXT_X_OFFSET = CONTENT_OFFSET;
    protected static final int TEXT_Y_OFFSET = CONTENT_OFFSET - 3;

    public TextWindow(Color windowColor, int offset) {
        super(windowColor, offset);
    }

    protected static Color getColor(String color) {
        if (color.matches("([0-9]+),([0-9]+),([0-9]+)")) {
            String[] rgb = color.split(",");
            return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
        }
        switch (color) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "white":
                return Color.WHITE;
            default:
                return null;
        }
    }

    public void setString(String string, Color fontColor, int fontSize) {
        List<TextWithStyle> textsWithStyle = new ArrayList<>();
        int offset = 0;
        Pattern groupPattern = Pattern.compile("#\\{(.*)\\|(.*)\\|(.*)\\}");
        Matcher groupMatcher;
        Pattern stringPattern = Pattern.compile("#\\{.*?\\}");
        Matcher stringMatcher = stringPattern.matcher(string);
        while (stringMatcher.find()) {
            String group = stringMatcher.group();
            groupMatcher = groupPattern.matcher(group);
            if (groupMatcher.matches()) {
                int start = stringMatcher.start();
                int end = stringMatcher.end();
                Color color = getColor(groupMatcher.group(1));
                String style = groupMatcher.group(2);
                String subText = groupMatcher.group(3);
                if (color == null) {
                    string = string.substring(0, end - offset).replace(group, Utils.stringRepeat(" ", subText.length())) + string.substring(end - offset);
                    String blank = string.substring(0, start - offset).replaceAll(".", " ");
                    textsWithStyle.add(new TextWithStyle(fontColor, style, blank + subText));
                } else if (color != fontColor) {
                    string = string.substring(0, end - offset).replace(group, Utils.stringRepeat(" ", subText.length())) + string.substring(end - offset);
                    String blank = string.substring(0, start - offset).replaceAll(".", " ");
                    textsWithStyle.add(new TextWithStyle(color, style, blank + subText));
                } else {
                    string = string.substring(0, end - offset).replace(group, subText) + string.substring(end - offset);
                }
                offset += group.length() - subText.length();
            }
        }
        textsWithStyle.add(0, new TextWithStyle(fontColor, "regular", string));
        this.texts = new ArrayList<>();
        int maxWidth = 0;
        int maxHeight = 0;
        for (TextWithStyle textWithStyle : textsWithStyle) {
            Text text = new Text();
            String fontName = "Monospace";
            if (textWithStyle.style.contains("bold")) {
                fontName += "Bold";
            }
            if (textWithStyle.style.contains("italic")) {
                fontName += "Italic";
            }
            text.setFont(Fonts.getFont(fontName));
            text.setCharacterSize(fontSize);
            text.setColor(textWithStyle.color);
            text.setString(textWithStyle.content);
            this.texts.add(text);

            FloatRect bounds = text.getLocalBounds();
            maxWidth = Utils.max(maxWidth, (int) bounds.left + (int) bounds.width + CONTENT_OFFSET * 2);
            maxHeight = (int) bounds.top + (int) bounds.height + CONTENT_OFFSET * 2;
        }
        this.width = maxWidth;
        this.height = maxHeight;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public void draw(List<RenderTexture> layers) {
        super.draw(layers);

        RenderTexture target = layers.get(this.offset + 1);
        Text drawText = new Text();
        this.texts.forEach(text -> {
            String string = text.getString();
            drawText.setPosition(text.getPosition().x + this.x + TEXT_X_OFFSET, text.getPosition().y + this.y + TEXT_Y_OFFSET);
            drawText.setFont(text.getFont());
            drawText.setColor(text.getColor());
            drawText.setCharacterSize(text.getCharacterSize());
            drawText.setString(string);
            target.draw(drawText);
        });
    }
}
