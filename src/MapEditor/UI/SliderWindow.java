package MapEditor.UI;

import Graphics.Fonts;
import UI.Window;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Text;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class SliderWindow extends Window {
    private List<Slider> sliders;
    private float size;
    private static final Text text = new Text();
    private static final DecimalFormat df = new DecimalFormat("###.###");

    public SliderWindow(float size, List<float[]> values) {
        super(new Color(100, 100, 255, 216), 3);
        this.size = size;
        this.sliders = values.stream().map(value -> new Slider(size, value[0], value[1], 0f)).collect(Collectors.toList());

        this.width = (int) size + CONTENT_OFFSET * 4 + 30;
        this.height = this.sliders.size() * 25 + CONTENT_OFFSET * 2 - 3;

        text.setFont(Fonts.getFont("Monospace"));
        text.setColor(Color.WHITE);
        text.setCharacterSize(13);
    }

    public boolean update() {
        return this.sliders.stream().anyMatch(Slider::update);
    }

    public void draw(List<RenderTexture> layers) {
        super.draw(layers);

        for (int i = 0; i < this.sliders.size(); i++) {
            Slider slider = this.sliders.get(i);
            text.setString(Locale.Locale.formatNumber((int) slider.getValue()));
            text.setPosition(this.x + CONTENT_OFFSET * 2 + 8 + this.size, this.y + CONTENT_OFFSET + 25 * i + 2);
            layers.get(this.offset + 1).draw(text);
            slider.draw(layers, this.offset + 1);
        }
    }

    public float getValue(int index) {
        return this.sliders.get(index).getValue();
    }

    @Override
    public void setTop(int y) {
        super.setTop(y);

        for (int i = 0; i < this.sliders.size(); i++) {
            Slider slider = this.sliders.get(i);
            slider.setPosition(slider.x, y + CONTENT_OFFSET + 9 + 25 * i);
        }
    }

    @Override
    public void setLeft(int x) {
        super.setLeft(x);

        for (Slider slider : this.sliders) {
            slider.setPosition(x + CONTENT_OFFSET * 2, slider.y);
        }
    }
}
