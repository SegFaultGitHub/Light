package MapEditor.UI;

import UI.Window;
import Utils.Visibility;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.system.Vector2f;

import java.util.List;

public class LightSourceWindow extends Window {
    private Slider rSlider;
    private Slider gSlider;
    private Slider bSlider;
    private Slider aSlider;
    private Slider radiusSlider;
    private Color color;

    private static RectangleShape rectangleShape = new RectangleShape();

    public LightSourceWindow() {
        super(new Color(100, 100, 255, 216), 3);
        this.rSlider = new Slider(
                100, 0, 255,
                1f
        );
        this.gSlider = new Slider(
                100, 0, 255,
                1f
        );
        this.bSlider = new Slider(
                100, 0, 255,
                1f
        );
        this.aSlider = new Slider(
                100, 0, 255,
                1f
        );
        this.radiusSlider = new Slider(
                210, 0, 1000,
                0.5f
        );

        rectangleShape.setSize(new Vector2f(100, 100));
        rectangleShape.setFillColor(Color.WHITE);

        this.width = 210 + CONTENT_OFFSET * 4;
        this.height = 100 + CONTENT_OFFSET * 2 + 22;

        this.setColor();
    }

    public void setLightSource(Visibility.LightSource lightSource) {
        this.rSlider.setValue(lightSource.color.r);
        this.gSlider.setValue(lightSource.color.g);
        this.bSlider.setValue(lightSource.color.b);
        this.aSlider.setValue(lightSource.color.a);
        this.radiusSlider.setValue(lightSource.radius);
        this.setColor();
    }

    public boolean update() {
        boolean updated = this.rSlider.update() ||
                this.gSlider.update() ||
                this.bSlider.update() ||
                this.aSlider.update() ||
                this.radiusSlider.update();

        this.setColor();
        return updated;
    }

    public void draw(List<RenderTexture> layers) {
        super.draw(layers);
        this.rSlider.draw(layers, this.offset + 1);
        this.gSlider.draw(layers, this.offset + 1);
        this.bSlider.draw(layers, this.offset + 1);
        this.aSlider.draw(layers, this.offset + 1);
        this.radiusSlider.draw(layers, this.offset + 1);

        layers.get(this.offset + 1).draw(rectangleShape);
    }

    @Override
    public void setTop(int y) {
        super.setTop(y);
        this.rSlider.setPosition(this.rSlider.x, y + CONTENT_OFFSET + 9);
        this.gSlider.setPosition(this.gSlider.x, y + CONTENT_OFFSET + 25 + 9);
        this.bSlider.setPosition(this.bSlider.x, y + CONTENT_OFFSET + 50 + 9);
        this.aSlider.setPosition(this.aSlider.x, y + CONTENT_OFFSET + 75 + 9);
        this.radiusSlider.setPosition(this.aSlider.x, y + CONTENT_OFFSET + 100 + 9);
        rectangleShape.setPosition(rectangleShape.getPosition().x, y + CONTENT_OFFSET);
    }

    @Override
    public void setLeft(int x) {
        super.setLeft(x);
        this.rSlider.setPosition(x + CONTENT_OFFSET * 2, this.rSlider.y);
        this.gSlider.setPosition(x + CONTENT_OFFSET * 2, this.gSlider.y);
        this.bSlider.setPosition(x + CONTENT_OFFSET * 2, this.bSlider.y);
        this.aSlider.setPosition(x + CONTENT_OFFSET * 2, this.aSlider.y);
        this.radiusSlider.setPosition(x + CONTENT_OFFSET * 2, this.radiusSlider.y);
        rectangleShape.setPosition(x + CONTENT_OFFSET * 2 + 110, rectangleShape.getPosition().y);
    }

    public Color getColor() {
        return new Color(
                (int) this.rSlider.getValue(),
                (int) this.gSlider.getValue(),
                (int) this.bSlider.getValue(),
                (int) this.aSlider.getValue()
        );
    }

    public float getRadius() {
        return this.radiusSlider.getValue();
    }

    private void setColor() {
        this.color = getColor();
        rectangleShape.setOutlineColor(this.color);
        rectangleShape.setOutlineThickness(-50);
    }
}
