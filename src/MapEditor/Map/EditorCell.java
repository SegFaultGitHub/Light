package MapEditor.Map;

import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.List;

public class EditorCell {
    public enum Type {
        WATER, JUMP_PAD, GRAVITY_PAD, LAVA, NONE, WALLS
    }

    public abstract static class CellData {}
    public static class JumpPadData extends CellData {
        public float jumpForce;

        public JumpPadData(float jumpForce) {
            this.jumpForce = jumpForce;
        }
    }

    private int x, y;
    public Type type;
    public CellData data;

    private static final RectangleShape rectangleShape = new RectangleShape();

    public EditorCell(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = Type.NONE;
        this.data = null;
    }

    public void draw(
            List<RenderTexture> layers,
            float cellWidth, float cellHeight,
            float xDrawOffset, float yDrawOffset
    ) {
        switch (this.type) {
            case NONE:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(new Color(230, 230, 230));
                layers.get(0).draw(rectangleShape);
                break;
            case WALLS:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(new Color(32, 32, 32));
                layers.get(0).draw(rectangleShape);
                break;
            case WATER:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(new Color(32, 32, 255));
                layers.get(0).draw(rectangleShape);
                break;
            case LAVA:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(new Color(32, 255, 32));
                layers.get(0).draw(rectangleShape);
                break;
            case JUMP_PAD:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(Color.RED);
                layers.get(0).draw(rectangleShape);
                float jumpSize = ((JumpPadData) this.data).jumpForce * cellHeight;
                Vertex[] vertices = new Vertex[] {
                        new Vertex(new Vector2f(
                                this.x * cellWidth - xDrawOffset + cellWidth / 2,
                                this.y * cellHeight - yDrawOffset + cellHeight / 2 - jumpSize
                        ), Color.RED),
                        new Vertex(new Vector2f(
                                this.x * cellWidth - xDrawOffset + cellWidth / 2,
                                this.y * cellHeight - yDrawOffset + cellHeight / 2 + jumpSize
                        ), Color.RED)
                };
                layers.get(1).draw(vertices, PrimitiveType.LINES);
                break;
            case GRAVITY_PAD:
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setOutlineThickness(-1);
                rectangleShape.setOutlineColor(new Color(Color.BLACK, 32));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
                rectangleShape.setFillColor(new Color(174, 0, 255));
                layers.get(0).draw(rectangleShape);
                break;
        }
    }
}
