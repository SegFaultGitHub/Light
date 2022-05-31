package MapEditor.Map;

import Map.Map;
import Map.Cell;
import org.jsfml.graphics.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;

public abstract class MapElement {
    public static class WallsElement extends MapElement {
        private int i, j;
        private int width, height;

        public WallsElement(JSONObject jsonObject) {
            this(
                    ((Long) jsonObject.get("i")).intValue(),
                    ((Long) jsonObject.get("j")).intValue(),
                    ((Long) jsonObject.get("width")).intValue(),
                    ((Long) jsonObject.get("height")).intValue()
            );
        }

        public WallsElement(int i, int j, int width, int height) {
            this.i = i;
            this.j = j;
            this.width = width;
            this.height = height;
        }

        public void constructMap(Map map) {
            for (int _i = this.i; _i < this.i + this.width; _i++) {
                for (int _j = this.j; _j < this.j + this.height; _j++) {
                    map.cells[_i][_j] = new Cell(true, _i, _j);
                }
            }
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "walls");
            jsonObject.put("i", this.i);
            jsonObject.put("j", this.j);
            jsonObject.put("width", this.width);
            jsonObject.put("height", this.height);

            return jsonObject;
        }
    }

    public static class WaterElement extends MapElement {
        private int i, j;
        private int width, height;

        public WaterElement(JSONObject jsonObject) {
            this(
                    ((Long) jsonObject.get("i")).intValue(),
                    ((Long) jsonObject.get("j")).intValue(),
                    ((Long) jsonObject.get("width")).intValue(),
                    ((Long) jsonObject.get("height")).intValue()
            );
        }

        public WaterElement(int i, int j, int width, int height) {
            this.i = i;
            this.j = j;
            this.width = width;
            this.height = height;
        }

        public void constructMap(Map map) {
            for (int _i = this.i; _i < this.i + this.width; _i++) {
                for (int _j = this.j; _j < this.j + this.height; _j++) {
                    map.cells[_i][_j] = new Cell.Water(_i, _j);
                }
            }
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "water");
            jsonObject.put("i", this.i);
            jsonObject.put("j", this.j);
            jsonObject.put("width", this.width);
            jsonObject.put("height", this.height);

            return jsonObject;
        }
    }

    public static class Lavaelement extends MapElement {
        private int i, j;
        private int width, height;

        public Lavaelement(JSONObject jsonObject) {
            this(
                    ((Long) jsonObject.get("i")).intValue(),
                    ((Long) jsonObject.get("j")).intValue(),
                    ((Long) jsonObject.get("width")).intValue(),
                    ((Long) jsonObject.get("height")).intValue()
            );
        }

        public Lavaelement(int i, int j, int width, int height) {
            this.i = i;
            this.j = j;
            this.width = width;
            this.height = height;
        }

        public void constructMap(Map map) {
            for (int _i = this.i; _i < this.i + this.width; _i++) {
                for (int _j = this.j; _j < this.j + this.height; _j++) {
                    map.cells[_i][_j] = new Cell.Lava(_i, _j);
                }
            }
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "lava");
            jsonObject.put("i", this.i);
            jsonObject.put("j", this.j);
            jsonObject.put("width", this.width);
            jsonObject.put("height", this.height);

            return jsonObject;
        }
    }

    public static class LightSourceElement extends MapElement {
        private float x, y;
        private float radius;
        private Color color;

        public LightSourceElement(JSONObject jsonObject) {
            this(
                    ((Double) jsonObject.get("x")).floatValue(),
                    ((Double) jsonObject.get("y")).floatValue(),
                    ((Double) jsonObject.get("radius")).floatValue(),
                    new Color(
                            ((Long) ((JSONArray) jsonObject.get("color")).get(0)).intValue(),
                            ((Long) ((JSONArray) jsonObject.get("color")).get(1)).intValue(),
                            ((Long) ((JSONArray) jsonObject.get("color")).get(2)).intValue(),
                            ((Long) ((JSONArray) jsonObject.get("color")).get(3)).intValue()
                    )
            );
        }

        public LightSourceElement(float x, float y, float radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        public void constructMap(Map map) {
            map.lightSources.add(new Utils.Visibility.LightSource(
                    new Utils.Visibility.Point(this.x * map.cellWidth, this.y * map.cellHeight),
                    this.radius,
                    this.color
            ));
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "lightSource");
            jsonObject.put("x", this.x);
            jsonObject.put("y", this.y);
            jsonObject.put("radius", this.radius);
            List<Integer> color = Arrays.asList(this.color.r, this.color.g, this.color.b, this.color.a);
            jsonObject.put("color", color);

            return jsonObject;
        }
    }

    public static class JumpPadElement extends MapElement {
        private int i, j;
        private float jumpForce;

        public JumpPadElement(JSONObject jsonObject) {
            this(
                    ((Long) jsonObject.get("i")).intValue(),
                    ((Long) jsonObject.get("j")).intValue(),
                    ((Double) jsonObject.get("jumpForce")).floatValue()
            );
        }

        public JumpPadElement(int i, int j, float jumpForce) {
            this.i = i;
            this.j = j;
            this.jumpForce = jumpForce;
        }

        public void constructMap(Map map) {
            map.cells[this.i][this.j] = new Cell.JumpPad(this.i, this.j, this.jumpForce);
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "jumpPad");
            jsonObject.put("i", this.i);
            jsonObject.put("j", this.j);
            jsonObject.put("jumpForce", this.jumpForce);

            return jsonObject;
        }
    }

    public static class GravityPadElement extends MapElement {
        private int i, j;

        public GravityPadElement(JSONObject jsonObject) {
            this(
                    ((Long) jsonObject.get("i")).intValue(),
                    ((Long) jsonObject.get("j")).intValue()
            );
        }

        public GravityPadElement(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void constructMap(Map map) {
            map.cells[this.i][this.j] = new Cell.GravityPad(this.i, this.j);
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "gravityPad");
            jsonObject.put("i", this.i);
            jsonObject.put("j", this.j);

            return jsonObject;
        }
    }

    public static class LaserElement extends MapElement {
        private float x, y;
        private float radius;
        private float[] amplitude;
        private float startAngle;
        private float speed;

        public LaserElement(JSONObject jsonObject) {
            this(
                    ((Double) jsonObject.get("x")).floatValue(),
                    ((Double) jsonObject.get("y")).floatValue(),
                    ((Double) jsonObject.get("radius")).floatValue(),
                    jsonObject.get("amplitude") == null ?
                            null :
                            new float[]{
                                    ((Double) ((JSONArray) jsonObject.get("amplitude")).get(0)).floatValue(),
                                    ((Double) ((JSONArray) jsonObject.get("amplitude")).get(1)).floatValue()
                            },
                    ((Double) jsonObject.get("startAngle")).floatValue(),
                    ((Double) jsonObject.get("speed")).floatValue()
            );
        }

        public LaserElement(float x, float y, float radius, float[] amplitude, float startAngle, float speed) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.amplitude = amplitude;
            this.startAngle = startAngle;
            this.speed = speed;
        }

        public void constructMap(Map map) throws Exception {
            map.lasers.add(new Cell.Laser(
                    this.x * map.cellWidth, this.y * map.cellHeight,
                    this.radius, this.amplitude, this.startAngle, this.speed
            ));
        }

        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "laser");
            jsonObject.put("x", this.x);
            jsonObject.put("y", this.y);
            jsonObject.put("radius", this.radius);
            List<Float> amplitude = Arrays.asList(this.amplitude[0], this.amplitude[1]);
            jsonObject.put("amplitude", amplitude);
            jsonObject.put("startAngle", this.startAngle);
            jsonObject.put("speed", this.speed);

            return jsonObject;
        }

    }

    public abstract void constructMap(Map map) throws Exception;

    public abstract JSONObject toJSON();
}
