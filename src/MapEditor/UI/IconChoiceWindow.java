package MapEditor.UI;

import Graphics.Textures;
import Globals.Globals;
import UI.TextWindow;
import UI.Window;
import Utils.Input;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;

import java.util.List;

public class IconChoiceWindow extends Window {
    private static final int ICON_SIZE = 24;
    private static final int ICON_OFFSET = 1;

    public static class Button {
        public Sprite icon;
        public Runnable function;
        public String text;

        public Button(Sprite icon, Runnable function, String text) {
            this.icon = icon;
            this.function = function;
            this.text = text;
        }
    }

    private List<Button> buttons;
    private int currentIndex;
    private TextWindow textWindow;
    private int selectedIndex;

    public IconChoiceWindow(
            int offset,
            List<Button> buttons
    ) {
        super(new Color(100, 100, 255, 216), offset);
        this.buttons = buttons;
        this.textWindow = null;
        this.currentIndex = -1;
        this.selectedIndex = 0;

        this.width = ICON_SIZE + CONTENT_OFFSET * 2 + ICON_OFFSET * 2;
        this.height = (ICON_SIZE + ICON_OFFSET * 2) * this.buttons.size() + CONTENT_OFFSET * 2;
    }

    public void run(int index) {
        this.selectedIndex = index;
        this.buttons.get(this.selectedIndex).function.run();
    }

    @Override
    public boolean update() {
        // Mouseover
        Vector2i mousePosition = Input.getCurrentMousePosition(this);
        if (mousePosition.x >= CONTENT_OFFSET + ICON_OFFSET &&
                mousePosition.x < CONTENT_OFFSET + ICON_OFFSET + ICON_SIZE + ICON_OFFSET * 2 &&
                mousePosition.y >= CONTENT_OFFSET + ICON_OFFSET &&
                mousePosition.y <= CONTENT_OFFSET + ICON_OFFSET + this.buttons.size() * (ICON_SIZE + ICON_OFFSET * 2)
        ) {
            int x = (mousePosition.x - CONTENT_OFFSET) / (ICON_SIZE + ICON_OFFSET * 2);
            int y = (mousePosition.y - CONTENT_OFFSET) / (ICON_SIZE + ICON_OFFSET * 2);
            int index = y + x;
            if ((mousePosition.x - CONTENT_OFFSET) % (ICON_SIZE + ICON_OFFSET * 2) >= ICON_OFFSET &&
                    (mousePosition.x - CONTENT_OFFSET) % (ICON_SIZE + ICON_OFFSET * 2) < ICON_SIZE + ICON_OFFSET &&
                    (mousePosition.y - CONTENT_OFFSET) % (ICON_SIZE + ICON_OFFSET * 2) >= ICON_OFFSET &&
                    (mousePosition.y - CONTENT_OFFSET) % (ICON_SIZE + ICON_OFFSET * 2) < ICON_SIZE + ICON_OFFSET &&
                    index < this.buttons.size()
            ) {
                if (this.currentIndex != index) {
                    this.currentIndex = index;
                    this.textWindow = new TextWindow(new Color(100, 100, 255, 216), this.offset + 1);
                    this.textWindow.setString(
                            this.buttons.get(this.currentIndex).text,
                            Color.WHITE,
                            16
                    );
                }
                if (Input.isLeftMousePressedOnce()) {
                    this.run(this.currentIndex);
                }
            } else {
                this.currentIndex = -1;
                this.textWindow = null;
            }
        } else {
            this.currentIndex = -1;
            this.textWindow = null;
        }

        if (this.textWindow != null) {
            this.textWindow.update();
            this.textWindow.setTopLeftPosition(mousePosition.x + leftBound() + 10, mousePosition.y + topBound() + 10, (int) Globals.SCREEN_WIDTH, (int) Globals.SCREEN_HEIGHT);
        }

        return true;
    }

    @Override
    public void draw(List<RenderTexture> layers) {
        super.draw(layers);

        RenderTexture target = layers.get(this.offset);

        RectangleShape rectangleShape = new RectangleShape();
        rectangleShape.setSize(new Vector2f(ICON_SIZE, ICON_SIZE));

        if (this.currentIndex != -1) {
            rectangleShape.setPosition(this.x + CONTENT_OFFSET + ICON_OFFSET, this.y + CONTENT_OFFSET + this.currentIndex * (ICON_SIZE + ICON_OFFSET * 2) + ICON_OFFSET);
            rectangleShape.setFillColor(new Color(Color.BLACK, 192));
            target.draw(rectangleShape);
        }

        Sprite selectedButton = Textures.getIcon(1, 0);

        for (int i = 0; i < this.buttons.size(); i++) {
            Sprite sprite = this.buttons.get(i).icon;
            rectangleShape.setPosition(this.x + CONTENT_OFFSET + ICON_OFFSET, this.y + CONTENT_OFFSET + i * (ICON_SIZE + ICON_OFFSET * 2) + ICON_OFFSET);
            rectangleShape.setFillColor(new Color(Color.BLACK, 96));

            sprite.setPosition(this.x + CONTENT_OFFSET + ICON_OFFSET, this.y + CONTENT_OFFSET + i * (ICON_SIZE + ICON_OFFSET * 2) + ICON_OFFSET);
            sprite.setScale((float) ICON_SIZE / (float) Textures.ICON_SIZE, (float) ICON_SIZE / (float) Textures.ICON_SIZE);

            if (i == this.selectedIndex) {
                selectedButton.setPosition(this.x + CONTENT_OFFSET + ICON_OFFSET, this.y + CONTENT_OFFSET + this.selectedIndex * (ICON_SIZE + ICON_OFFSET * 2) + ICON_OFFSET);
                selectedButton.setScale((float) ICON_SIZE / (float) Textures.ICON_SIZE, (float) ICON_SIZE / (float) Textures.ICON_SIZE);

                target.draw(selectedButton);
            } else {
                target.draw(rectangleShape);
            }
            target.draw(sprite);
        }

        if (this.textWindow != null) {
            this.textWindow.draw(layers);
        }
    }
}
