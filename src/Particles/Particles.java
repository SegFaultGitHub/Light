package Particles;

import Map.Map;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Particles {
    public static class Particle {
        private float x, y;
        private float speedX, speedY;
        private RectangleShape rectangleShape;
        private Color color;

        public Particle(float size, float x, float y, float speedX, float speedY, Color color) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.color = color;

            this.rectangleShape = new RectangleShape();
            this.rectangleShape.setFillColor(color);
            this.rectangleShape.setSize(new Vector2f(size, size));
        }

        public void update(Map map) {
            this.speedY += Math.abs(map.gravity);
            this.x += this.speedX;
            this.y += this.speedY;

            this.color = new Color(this.color, this.color.a - 10);
            this.rectangleShape.setFillColor(this.color);
        }

        public void draw(List<RenderTexture> layers, int offset, float xDrawOffset, float yDrawOffset) {
            this.rectangleShape.setPosition(this.x - xDrawOffset, this.y - yDrawOffset);
            layers.get(offset).draw(this.rectangleShape);
        }

        public boolean isAlive() {
            return this.color.a > 0;
        }
    }

    private static List<Particle> particles;

    public static void initialize() {
        particles = new ArrayList<>();
    }

    public static void update(Map map) {
        particles.forEach(particle -> particle.update(map));
        particles = particles.stream().filter(Particle::isAlive).collect(Collectors.toList());
    }

    public static void draw(List<RenderTexture> layers, int offset, float xDrawOffset, float yDrawOffset) {
        particles.forEach(particle -> particle.draw(layers, offset, xDrawOffset, yDrawOffset));
    }

    public static void generateParticle(float size, float x, float y, float speedX, float speedY, Color color) {
        particles.add(new Particle(
            size, x, y, speedX, speedY, color
        ));
    }
}
