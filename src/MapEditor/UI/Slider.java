package MapEditor.UI;

import Graphics.Fonts;
import Utils.Input;
import Utils.Utils;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;

import java.util.List;

public class Slider {
    private float ratio;
    private float size;
    private float min, max;
    public float x, y;

    private boolean sliding;

    private static final float sliderHeight = 4;
    private static final Vector2f sliderButtonSize = new Vector2f(6, 14);
    private static final RectangleShape rectangleShape = new RectangleShape();
    private Text textValue;

    public Slider(float size, float min, float max, float ratio) {
        this.size = size;
        this.min = min;
        this.max = max;

        this.ratio = ratio;

        this.textValue = new Text();
        this.textValue.setPosition(this.x + this.size + 10, this.y);
        this.textValue.setCharacterSize(10);
        this.textValue.setFont(Fonts.getFont("Monospace"));
        this.textValue.setColor(Color.BLACK);
        this.setTextValue();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean update() {
        if (
                Input.isMouseInRect(
                        this.x - sliderButtonSize.x / 2 + this.ratio * this.size,
                        this.y - sliderButtonSize.y / 2 + sliderHeight / 2,
                        sliderButtonSize.x, sliderButtonSize.y
                )
        ) {
            if (Input.isLeftMousePressed()) this.sliding = true;
        }
        if (Input.isLeftMouseReleased()) this.sliding = false;

        if (this.sliding) {
            float mousePosX = Input.getCurrentMousePositionF().x;
            this.ratio = Utils.min(1f, (Utils.max(this.x, mousePosX) - this.x) / this.size);
            this.setTextValue();
            return true;
        }
        return false;
    }

    public void draw(List<RenderTexture> layers, int offset) {
        rectangleShape.setPosition(this.x, this.y);
        rectangleShape.setOutlineThickness(0);
        rectangleShape.setFillColor(new Color(Color.BLACK, 128));
        rectangleShape.setSize(new Vector2f(this.size, sliderHeight));
        layers.get(offset).draw(rectangleShape);

        rectangleShape.setPosition(
                this.x - sliderButtonSize.x / 2 + this.ratio * this.size,
                this.y - sliderButtonSize.y / 2 + sliderHeight / 2
        );
        rectangleShape.setOutlineThickness(1);
        rectangleShape.setOutlineColor(Color.BLACK);
        rectangleShape.setFillColor(Color.WHITE);
        rectangleShape.setSize(sliderButtonSize);
        layers.get(offset + 1).draw(rectangleShape);

//        layers.get(offset).draw(this.textValue);
    }

    public void setValue(float value) {
        if (value <= this.min) this.ratio = 0;
        if (value >= this.max) this.ratio = 1;
        this.ratio = (value - this.min) / (this.max - this.min);
    }

    public float getValue() {
        return (this.max - this.min) * this.ratio + this.min;
    }

    private void setTextValue() {
        this.textValue.setString(String.valueOf(getValue()));
    }
}
