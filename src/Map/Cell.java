package Map;

import Player.Player;
import Utils.Utils;
import Utils.Visibility;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.List;

import static Utils.Visibility.intersection;

public class Cell {
    public static class JumpPad extends Cell {
        private float force;
        private static CircleShape circleShape = new CircleShape();

        public JumpPad(int i, int j, float force) {
            super(false, i, j);
            this.force = force;
            this.disabled = false;
        }

        @Override
        public void applyEffect(Map map, Player player) {
            if (this.disabled) return;
            this.disabled = true;
            player.forceJump(map, map.getJumpForce(this.force), false, false);
        }

        @Override
        public void draw(
                List<RenderTexture> layers, int offset,
                float cellWidth, float cellHeight,
                float xDrawOffset, float yDrawOffset,
                Cell cellAbove, Cell cellBelow
        ) {
            super.draw(layers, offset, cellWidth, cellHeight, xDrawOffset, yDrawOffset, cellAbove, cellBelow);
            circleShape.setPointCount(16);
            circleShape.setFillColor(this.disabled ? new Color(Color.BLACK, 64) : Color.RED);
            float radius = Utils.min(cellWidth, cellHeight) * 0.8f / 2;
            circleShape.setRadius(radius);
            circleShape.setOrigin(new Vector2f(radius, radius));
            circleShape.setPosition(this.x * cellWidth + cellWidth / 2 - xDrawOffset, this.y * cellHeight + cellHeight / 2 - yDrawOffset);
            layers.get(offset).draw(circleShape);
        }
    }

    public static class GravityPad extends Cell {
        private static CircleShape circleShape = new CircleShape();

        public GravityPad(int i, int j) {
            super(false, i, j);
            this.disabled = false;
        }

        @Override
        public void applyEffect(Map map, Player player) {
            if (this.disabled) return;
            this.disabled = true;
            map.gravity *= -1;
            player.speedY = 0;
        }

        @Override
        public void draw(
                List<RenderTexture> layers, int offset,
                float cellWidth, float cellHeight,
                float xDrawOffset, float yDrawOffset,
                Cell cellAbove, Cell cellBelow
        ) {
            super.draw(layers, offset, cellWidth, cellHeight, xDrawOffset, yDrawOffset, cellAbove, cellBelow);
            circleShape.setPointCount(16);
            circleShape.setFillColor(this.disabled ? new Color(Color.BLACK, 64) : new Color(174, 0, 255));
            float radius = Utils.min(cellWidth, cellHeight) * 0.8f / 2;
            circleShape.setRadius(radius);
            circleShape.setOrigin(new Vector2f(radius, radius));
            circleShape.setPosition(this.x * cellWidth + cellWidth / 2 - xDrawOffset, this.y * cellHeight + cellHeight / 2 - yDrawOffset);
            layers.get(offset).draw(circleShape);
        }
    }

    public static class Water extends Cell {
        private static final float waterSurfaceRatio = 0.85f;
        private static final float[] blueRatio = new float[] { 128, 255 };
        private float blueGradientRatio;
        private int blueGradientSpeed;

        public Water(int i, int j) {
            super(false, i, j);
            this.disabled = false;

            this.blueGradientRatio = Utils.getRandomFloat(0, 1);
            this.blueGradientSpeed = Utils.getRandomInt(0, 1) == 0 ? 1 : -1;
        }

        @Override
        public void applyEffect(Map map, Player player) {
            if (this.disabled) return;
            map.playerBlind = true;
        }

        @Override
        public void draw(
                List<RenderTexture> layers, int offset,
                float cellWidth, float cellHeight,
                float xDrawOffset, float yDrawOffset,
                Cell cellAbove, Cell cellBelow
        ) {
            super.draw(layers, offset, cellWidth, cellHeight, xDrawOffset, yDrawOffset, cellAbove, cellBelow);
            if (cellAbove == null || (cellAbove.getClass() == Cell.class && !cellAbove.opaque)) {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight * waterSurfaceRatio));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset + cellHeight * (1 - waterSurfaceRatio));
            } else if (cellBelow == null || (cellBelow.getClass() == Cell.class && !cellBelow.opaque)) {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight * waterSurfaceRatio));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
            } else {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
            }
            rectangleShape.setOutlineThickness(0);

            this.blueGradientRatio += this.blueGradientSpeed * Utils.getRandomFloat(0.01f, 0.05f);
            if (this.blueGradientRatio > 1) {
                this.blueGradientRatio = 1;
                this.blueGradientSpeed = -1;
            } else if (this.blueGradientRatio < 0) {
                this.blueGradientRatio = 0;
                this.blueGradientSpeed = 1;
            }

            int blue = (int) (this.blueGradientRatio * (blueRatio[1] - blueRatio[0]) + blueRatio[0]);
            rectangleShape.setFillColor(new Color(0, 0, blue, 128));
            layers.get(offset).draw(rectangleShape);
        }
    }

    public static class Lava extends Cell {
        private static final float lavaSurfaceRatio = 0.85f;
        private static final float[] greenRatio = new float[] { 192, 255 };
        private float greenGradientRatio;
        private int greenGradientSpeed;

        public Lava(int i, int j) {
            super(false, i, j);
            this.disabled = false;

            this.greenGradientRatio = Utils.getRandomFloat(0, 1);
            this.greenGradientSpeed = Utils.getRandomInt(0, 1) == 0 ? 1 : -1;
        }

        @Override
        public void applyEffect(Map map, Player player) {
            if (this.disabled) return;
            player.die(map);
        }

        @Override
        public void draw(
                List<RenderTexture> layers, int offset,
                float cellWidth, float cellHeight,
                float xDrawOffset, float yDrawOffset,
                Cell cellAbove, Cell cellBelow
        ) {
            super.draw(layers, offset, cellWidth, cellHeight, xDrawOffset, yDrawOffset, cellAbove, cellBelow);
            if (cellAbove == null || (cellAbove.getClass() == Cell.class && !cellAbove.opaque)) {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight * lavaSurfaceRatio));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset + cellHeight * (1 - lavaSurfaceRatio));
            } else if (cellBelow == null || (cellBelow.getClass() == Cell.class && !cellBelow.opaque)) {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight * lavaSurfaceRatio));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
            } else {
                rectangleShape.setSize(new Vector2f(cellWidth, cellHeight));
                rectangleShape.setPosition(this.x * cellWidth - xDrawOffset, this.y * cellHeight - yDrawOffset);
            }
            rectangleShape.setOutlineThickness(0);

            this.greenGradientRatio += this.greenGradientSpeed * Utils.getRandomFloat(0.03f, 0.1f);
            if (this.greenGradientRatio > 1) {
                this.greenGradientRatio = 1;
                this.greenGradientSpeed = -1;
            } else if (this.greenGradientRatio < 0) {
                this.greenGradientRatio = 0;
                this.greenGradientSpeed = 1;
            }

            int green = (int) (this.greenGradientRatio * (greenRatio[1] - greenRatio[0]) + greenRatio[0]);
            rectangleShape.setFillColor(new Color(green, green / 2 - 50, 0, 192));
            layers.get(offset).draw(rectangleShape);
        }
    }

    public static class Laser {
        private float x, y;
        private float radius;
        private float angle;
        private float[] amplitude;
        private float speed;
        private float collisionRadius;
        private Visibility.Point collisionPoint;

        public Laser(float x, float y, float radius, float[] amplitude, float startAngle, float speed) throws Exception {
            this.x = x;
            this.y = y;
            this.radius = radius;
            if (amplitude != null) {
                this.amplitude = amplitude;
                if (this.amplitude[0] > this.amplitude[1]) throw new Exception("Amplitude");
                this.amplitude[0] = this.amplitude[0] / 180 * (float) Math.PI;
                this.amplitude[1] = this.amplitude[1] / 180 * (float) Math.PI;
            }
            this.speed = speed / 180 * (float) Math.PI;
            this.angle = startAngle / 180 * (float) Math.PI;
        }

        public void update(Map map, Player player) {
            if (this.amplitude != null) {
                this.angle += Utils.max(
                        Math.abs(this.speed / 10),
                        Utils.min(
                                Math.abs(this.speed),
                                (this.amplitude[1] - this.angle) / 5,
                                (this.angle - this.amplitude[0]) / 5
                        )
                ) * Math.signum(this.speed);
                if (this.angle >= this.amplitude[1]) {
                    this.angle = this.amplitude[1];
                    this.speed *= -1;
                } else if (this.angle <= this.amplitude[0]) {
                    this.angle = this.amplitude[0];
                    this.speed *= -1;
                }
            } else {
                this.angle += this.speed;
            }
            this.collisionPoint = intersection(
                    new Visibility.Point(this.x, this.y),
                    new Visibility.Point((float) (this.x + Math.cos(this.angle) * this.radius), (float) (this.y + Math.sin(this.angle) * this.radius)),
                    map.segments
            );
            float dx = this.collisionPoint.x - this.x;
            float dy = this.collisionPoint.y - this.y;
            this.collisionRadius = (float) Math.sqrt(dx * dx + dy * dy);
            if (this.collisionRadius < this.radius * 0.99f) {
                Particles.Particles.generateParticle(
                        Utils.getRandomFloat(1f, 2.5f),
                        this.collisionPoint.x, this.collisionPoint.y,
                        Utils.getRandomFloat(-5, 5), Utils.getRandomFloat(-5, 5),
                        Color.RED
                );
            }
            if (this.collideWithPlayer(player)) player.die(map);
        }

        public void draw(
                List<RenderTexture> layers, int offset,
                float xDrawOffset, float yDrawOffset
        ) {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(2.5f);
            circleShape.setOrigin(2.5f, 2.5f);
            circleShape.setPosition(this.collisionPoint.x - xDrawOffset, this.collisionPoint.y - yDrawOffset);
            circleShape.setFillColor(Color.RED);
            layers.get(offset).draw(circleShape);
            Vertex[] vertices = new Vertex[]{
                    new Vertex(new Vector2f(this.x - xDrawOffset, this.y - yDrawOffset), Color.RED),
                    new Vertex(new Vector2f(this.collisionPoint.x - xDrawOffset, this.collisionPoint.y - yDrawOffset), Color.RED)
            };
            layers.get(offset).draw(vertices, PrimitiveType.LINES);
        }

        public boolean collideWithPlayer(Player player) {
            Visibility.Point collisionPoint = intersection(
                    new Visibility.Point(this.x, this.y), this.collisionPoint,
                    player.segments
            );
            float dx = collisionPoint.x - this.x;
            float dy = collisionPoint.y - this.y;
            float collisionRadius = (float) Math.sqrt(dx * dx + dy * dy);
            return collisionRadius < this.collisionRadius * 0.99f;
        }
    }

    private static final RectangleShape rectangleShape = new RectangleShape();

    public int x, y;
    public boolean opaque;

    public boolean visited;

    public boolean disabled;

    public Cell(boolean opaque, int i, int j) {
        this.opaque = opaque;
        this.x = i;
        this.y = j;
        this.visited = false;
    }

    public void applyEffect(Map map, Player player) {}

    public void draw(
            List<RenderTexture> layers, int offset,
            float cellWidth, float cellHeight,
            float xDrawOffset, float yDrawOffset,
            Cell cellAbove, Cell cellBelow
    ) {}
}
