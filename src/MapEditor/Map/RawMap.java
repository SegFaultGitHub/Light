package MapEditor.Map;

import Utils.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RawMap {
    public List<MapElement> mapElements;
    public int width, height;
    public float cellWidth, cellHeight;
    public int playerStartPositionX;
    public int playerStartPositionY;
    public float playerWidth, playerHeight;
    public float gravity;
    public float dashDistance;
    public float speed;
    public float playerViewRadius;
    public float playerBlindViewRadius;
    public float jumpForce;
    public float extraJumpForce;

    public RawMap() {}

    public RawMap(String path) throws Exception {
        JSONObject jsonObject = Utils.readJSON(path);
        this.width = ((Long) jsonObject.get("width")).intValue();
        this.height = ((Long) jsonObject.get("height")).intValue();
        this.cellWidth = ((Double) jsonObject.get("cellWidth")).floatValue();
        this.cellHeight = ((Double) jsonObject.get("cellHeight")).floatValue();
        this.playerStartPositionX = ((Long) ((JSONArray) jsonObject.get("playerStartPosition")).get(0)).intValue();
        this.playerStartPositionY = ((Long) ((JSONArray) jsonObject.get("playerStartPosition")).get(1)).intValue();
        this.playerWidth = ((Double) jsonObject.get("playerWidth")).floatValue();
        this.playerHeight = ((Double) jsonObject.get("playerHeight")).floatValue();
        this.gravity = ((Double) jsonObject.get("gravity")).floatValue();
        this.dashDistance = ((Double) jsonObject.get("dashDistance")).floatValue();
        this.speed = ((Double) jsonObject.get("speed")).floatValue();
        this.playerViewRadius = ((Double) jsonObject.get("playerViewRadius")).floatValue();
        this.playerBlindViewRadius = ((Double) jsonObject.get("playerBlindViewRadius")).floatValue();
        this.jumpForce = ((Double) jsonObject.get("jumpForce")).floatValue();
        this.extraJumpForce = ((Double) jsonObject.get("extraJumpForce")).floatValue();

        this.mapElements = new ArrayList<>();
        for (Object object : (JSONArray) jsonObject.get("elements")) {
            JSONObject _jsonObject = (JSONObject) object;
            String type = (String) _jsonObject.get("type");
            switch (type) {
                case "walls":
                    this.mapElements.add(new MapElement.WallsElement(_jsonObject));
                    break;
                case "water":
                    this.mapElements.add(new MapElement.WaterElement(_jsonObject));
                    break;
                case "lava":
                    this.mapElements.add(new MapElement.Lavaelement(_jsonObject));
                    break;
                case "lightSource":
                    this.mapElements.add(new MapElement.LightSourceElement(_jsonObject));
                    break;
                case "jumpPad":
                    this.mapElements.add(new MapElement.JumpPadElement(_jsonObject));
                    break;
                case "gravityPad":
                    this.mapElements.add(new MapElement.GravityPadElement(_jsonObject));
                    break;
                case "laser":
                    this.mapElements.add(new MapElement.LaserElement(_jsonObject));
                    break;
            }
        }
    }


    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("width", this.width);
        jsonObject.put("height", this.height);
        jsonObject.put("cellWidth", this.cellWidth);
        jsonObject.put("cellHeight", this.cellHeight);
        jsonObject.put("playerStartPosition", Arrays.asList(this.playerStartPositionX, this.playerStartPositionY));
        jsonObject.put("playerWidth", this.playerWidth);
        jsonObject.put("playerHeight", this.playerHeight);
        jsonObject.put("gravity", this.gravity);
        jsonObject.put("dashDistance", this.dashDistance);
        jsonObject.put("speed", this.speed);
        jsonObject.put("playerViewRadius", this.playerViewRadius);
        jsonObject.put("playerBlindViewRadius", this.playerBlindViewRadius);
        jsonObject.put("jumpForce", this.jumpForce);
        jsonObject.put("extraJumpForce", this.extraJumpForce);
        JSONArray elements = new JSONArray();
        this.mapElements.forEach(mapElement -> elements.add(mapElement.toJSON()));
        jsonObject.put("elements", elements);

        return jsonObject;
    }
}
