package Map;

import Globals.Globals;
import Main.GameLoop;
import MapEditor.Map.MapElement;
import MapEditor.Map.RawMap;
import Player.Player;
import Utils.Utils;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static Utils.Visibility.*;

public class Map {

    private int width, height;
    public Cell[][] cells;
    public float cellWidth, cellHeight;

    public int[] playerStartPosition;
    public float playerWidth, playerHeight;
    public float gravity;
    // Total cells travelled
    public float dashDistance;
    // Pixels traveled per frame
    public float speed;
    public float playerViewRadius;
    public float playerBlindViewRadius;
    public float jumpForce;
    public float extraJumpForce;

    public List<Segment> segments;
    private LightSource playerLightSource;
    public List<LightSource> lightSources;

    public List<Cell.Laser> lasers;

    public float xDrawOffset, yDrawOffset;
    private float backgroundHeightSpeed;
    private float backgroundHeight;

    private float currentViewRadius;
    public boolean playerBlind;
    private float blindessRatio;

    private float savedGravityDirection;

    private static RenderTexture partialShadeTexture;
    private static RenderTexture completeShadeTexture;
    private RenderTexture shadeTexture;

    private static List<RenderTexture> backgroundTextures;

    public Map(RawMap rawMap) throws Exception {
        this.width = rawMap.width;
        this.height = rawMap.height;
        this.cellWidth = rawMap.cellWidth;
        this.cellHeight = rawMap.cellHeight;
        this.cells = new Cell[this.width][this.height];
        this.playerStartPosition = new int[]{rawMap.playerStartPositionX, rawMap.playerStartPositionY};
        this.playerWidth = rawMap.playerWidth;
        this.playerHeight = rawMap.playerHeight;
        this.gravity = rawMap.gravity;
        this.dashDistance = rawMap.dashDistance;
        this.speed = rawMap.speed;
        this.playerViewRadius = rawMap.playerViewRadius;
        this.playerBlindViewRadius = rawMap.playerBlindViewRadius;
        this.jumpForce = rawMap.jumpForce;
        this.extraJumpForce = rawMap.extraJumpForce;
        this.lightSources = new ArrayList<>();
        this.lasers = new ArrayList<>();

        for (MapElement mapElement : rawMap.mapElements) mapElement.constructMap(this);

        this.jumpForce = getJumpForce(3.4f);
        this.extraJumpForce = getJumpForce(2.4f);

        this.calculateRectangles();
        partialShadeTexture = new RenderTexture();
        partialShadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));
        completeShadeTexture = new RenderTexture();
        completeShadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));
        this.shadeTexture = new RenderTexture();
        this.shadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));

        this.playerBlind = false;
        this.blindessRatio = 0;
        this.currentViewRadius = this.playerViewRadius;

        this.backgroundHeightSpeed = 0;
        this.backgroundHeight = 0;

        this.savedGravityDirection = Math.signum(this.gravity);

        drawLightSourcesShadeTexture();
        drawBackgroundTextures();
    }

    private void drawBackgroundTextures() throws Exception {
        backgroundTextures = new ArrayList<>();
        RectangleShape rectangleShape = new RectangleShape();
        rectangleShape.setSize(new Vector2f(2, 2));
        int layerCount = 5;
        for (int i = 0; i < layerCount; i++) {
            float ratio = (float) (layerCount - i) / layerCount;
//            rectangleShape.setFillColor(Color.BLACK);
            rectangleShape.setFillColor(new Color((int) (255 * ratio), (int) (255 * ratio), (int) (255 * ratio), (int) (255 * ratio)));
            backgroundTextures.add(GameLoop.getTexture());
            float surface = Globals.SCREEN_WIDTH * Globals.SCREEN_HEIGHT;
            float dotCount = surface / 2500;
            for (int j = 0; j < dotCount; j++) {
                rectangleShape.setPosition(new Vector2f(Utils.getRandomFloat(0f, Globals.SCREEN_WIDTH), Utils.getRandomFloat(0f, Globals.SCREEN_HEIGHT)));
                backgroundTextures.get(i).draw(rectangleShape);
            }
        }
    }

    private void drawLightSourcesShadeTexture() {
        this.shadeTexture.clear(Color.BLACK);
        this.lightSources.forEach(lightSource -> {
            calculateVisibility(lightSource, loadMap(this.segments, lightSource.source));
            lightSource.currentRadius = lightSource.radius;
            completeShadeTexture.clear(Color.BLACK);
            drawFieldOfView(partialShadeTexture, completeShadeTexture, lightSource, lightSource.color);
            drawRangeView(partialShadeTexture, completeShadeTexture, lightSource);
            completeShadeTexture.display();
            this.shadeTexture.draw(new Sprite(completeShadeTexture.getTexture()), new RenderStates(BlendMode.ADD));
        });
        this.shadeTexture.display();
    }

    public void update(Player player) {
        this.lasers.forEach(laser -> laser.update(this, player));
        float goalViewRadius = this.playerBlind ? this.playerBlindViewRadius : this.playerViewRadius;
        if (goalViewRadius > this.currentViewRadius) {
            this.currentViewRadius += Utils.max(3f, (float) Math.ceil((goalViewRadius - this.currentViewRadius) / 100f));
            if (goalViewRadius < this.currentViewRadius) this.currentViewRadius = goalViewRadius;
        } else if (goalViewRadius < this.currentViewRadius) {
            this.currentViewRadius -= Utils.max(3f, (float) Math.ceil(goalViewRadius / 100f));
            if (goalViewRadius > this.currentViewRadius) this.currentViewRadius = goalViewRadius;
        }
        this.playerLightSource = new LightSource(
                new Point(player.x + player.width / 2f, player.y + player.height / 2f),
                this.currentViewRadius,
                new Color(250, 242, 211)
        );
        this.playerLightSource.currentRadius = this.playerLightSource.radius;
        if (this.playerLightSource.source.x - (int) this.playerLightSource.source.x == 0)
            this.playerLightSource.source.x += 0.1f;
        if (this.playerLightSource.source.y - (int) this.playerLightSource.source.y == 0)
            this.playerLightSource.source.y += 0.1f;
        calculateVisibility(this.playerLightSource, loadMap(this.segments, this.playerLightSource.source));

        if (this.playerBlind) {
            this.blindessRatio += 0.05f;
        } else {
            this.blindessRatio -= 0.05f;
        }
        this.blindessRatio = Utils.constrain(this.blindessRatio, 0f, 1f);

        this.calculateDrawOffset(player);
        if (Math.abs(this.backgroundHeightSpeed) <= Math.abs(Math.signum(this.gravity) * 10)) {
            this.backgroundHeightSpeed += 0.5f * Math.signum(this.gravity);
        }
        if (Math.abs(this.backgroundHeightSpeed) > Math.abs(Math.signum(this.gravity) * 10)) {
            this.backgroundHeightSpeed = Math.signum(this.gravity) * 10;
        }
        this.backgroundHeight += this.backgroundHeightSpeed;
    }

    public void draw(List<RenderTexture> layers, RenderTexture shadeLayer) {
        drawBackground(layers);
        drawCells(layers);
        drawShades(shadeLayer);
        this.lasers.forEach(laser -> laser.draw(layers, 3, this.xDrawOffset, this.yDrawOffset));
    }

    public void placePlayer(Player player) {
        player.setPositionOnMap(this, this.playerStartPosition[0], this.playerStartPosition[1]);
        this.gravity = Math.abs(this.gravity) * this.savedGravityDirection;
        player.resetMovementAttributes();
    }

    private void drawBackground(List<RenderTexture> layers) {
        for (int i = 0; i < backgroundTextures.size(); i++) {
            Sprite sprite = new Sprite(backgroundTextures.get(i).getTexture());
            float anchorX = (((-this.xDrawOffset / ((i + 1) * 3)) % Globals.SCREEN_WIDTH) + Globals.SCREEN_WIDTH) % Globals.SCREEN_WIDTH;
            float anchorY = (((-(this.yDrawOffset + this.backgroundHeight) / ((i + 1) * 3)) % Globals.SCREEN_HEIGHT) + Globals.SCREEN_HEIGHT) % Globals.SCREEN_HEIGHT;

            sprite.setPosition(anchorX, anchorY);
            layers.get(0).draw(sprite);

            sprite.setPosition(anchorX, anchorY - Globals.SCREEN_HEIGHT);
            layers.get(0).draw(sprite);

            sprite.setPosition(anchorX - Globals.SCREEN_WIDTH, anchorY);
            layers.get(0).draw(sprite);

            sprite.setPosition(anchorX - Globals.SCREEN_WIDTH, anchorY - Globals.SCREEN_HEIGHT);
            layers.get(0).draw(sprite);
        }
    }

    private void drawCells(List<RenderTexture> layers) {
        int iStart = Utils.max(0, (int) (this.xDrawOffset / this.cellWidth));
        int jStart = Utils.max(0, (int) (this.yDrawOffset / this.cellHeight));
        for (int i = 0; i + iStart < this.width && i <= (int) (Globals.SCREEN_WIDTH / this.cellWidth) + 1; i++) {
            for (int j = 0; j + jStart < this.height && j <= (int) (Globals.SCREEN_HEIGHT / this.cellHeight) + 1; j++) {
                Cell cellAbove = j + jStart - 1 < 0 ? null : this.cells[i + iStart][j + jStart - 1];
                Cell cellBelow = j + jStart + 1 >= this.height ? null : this.cells[i + iStart][j + jStart + 1];
                Cell cell = this.cells[i + iStart][j + jStart];
                if (cell != null) cell.draw(
                        layers, 2,
                        this.cellWidth, this.cellHeight,
                        this.xDrawOffset, this.yDrawOffset,
                        cellAbove, cellBelow
                );
            }
        }
    }

    private void drawShades(RenderTexture shadeLayer) {
        shadeLayer.clear(Color.BLACK);
        Sprite sprite = new Sprite();
        sprite.setTextureRect(new IntRect(
                (int) this.xDrawOffset, (int) this.yDrawOffset,
                (int) Globals.SCREEN_WIDTH, (int) Globals.SCREEN_HEIGHT
        ));
        if (this.blindessRatio < 1) {
            sprite.setTexture(this.shadeTexture.getTexture());
            sprite.setColor(new Color(Color.WHITE, (int) ((1f - this.blindessRatio) * 255f)));
            shadeLayer.draw(sprite);
        }

        sprite.setColor(Color.WHITE);
        completeShadeTexture.clear(Color.BLACK);
        drawFieldOfView(partialShadeTexture, completeShadeTexture, this.playerLightSource, Color.WHITE);
        completeShadeTexture.display();
        sprite.setTexture(completeShadeTexture.getTexture());
        shadeLayer.draw(sprite, new RenderStates(BlendMode.MULTIPLY));

        completeShadeTexture.clear(Color.BLACK);
        drawFieldOfView(partialShadeTexture, completeShadeTexture, this.playerLightSource, this.playerLightSource.color);
        drawRangeView(partialShadeTexture, completeShadeTexture, this.playerLightSource);
        completeShadeTexture.display();
        sprite.setTexture(completeShadeTexture.getTexture());
        shadeLayer.draw(sprite, new RenderStates(BlendMode.ADD));
    }

    public float getJumpForce(float height) {
        height = Math.abs(height) * this.cellHeight;
        float deltaSqrt = (float) Math.sqrt(1 + (4 * height * 2) / Math.abs(this.gravity));
        return 0.5f * (-1 + deltaSqrt) * Math.abs(this.gravity);
    }

    public boolean collision(int i, int j) {
        if (!inWidthBounds(i) || !inHeightBounds(j)) return false;
        return this.cells[i][j] != null && this.cells[i][j].opaque;
    }

    public boolean inWidthBounds(int i) {
        return Utils.inBounds(i, 0, this.width);
    }

    public boolean inHeightBounds(int j) {
        return Utils.inBounds(j, 0, this.height);
    }

    public Cell getCell(int i, int j) {
        return this.cells[i][j];
    }

    private void calculateDrawOffset(Player player) {
        if (this.width * this.cellWidth < Globals.SCREEN_WIDTH) {
            this.xDrawOffset = (this.width * this.cellWidth - Globals.SCREEN_WIDTH) / 2;
        } else {
            this.xDrawOffset = Utils.min(
                    this.width * this.cellWidth - Globals.SCREEN_WIDTH,
                    Utils.max(
                            0f,
                            player.x + player.width / 2f - Globals.SCREEN_WIDTH / 2
                    )
            );
        }
        if (this.height * this.cellHeight < Globals.SCREEN_HEIGHT) {
            this.yDrawOffset = (this.height * this.cellHeight - Globals.SCREEN_HEIGHT) / 2;
        } else {
            this.yDrawOffset = Utils.min(
                    this.height * this.cellHeight - Globals.SCREEN_HEIGHT,
                    Utils.max(
                            0f,
                            player.y + player.height / 2f - Globals.SCREEN_HEIGHT / 2
                    )
            );
        }
    }

    private void calculateRectangles() {
        this.segments = new ArrayList<>();
        this.segments.addAll(segmentsFromRectangle(new Rectangle(
                0, 0,
                this.width * this.cellWidth, this.height * this.cellHeight
        )));
        boolean[][] visited = new boolean[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (visited[i][j] || this.cells[i][j] == null || !this.cells[i][j].opaque) continue;

                int ii = i;
                while (ii < this.width && this.cells[ii][j] != null && this.cells[ii][j].opaque) ii++;

                int jj = j;
                boolean _continue = true;
                while (jj < this.height) {
                    for (int k = i; k < ii; k++) {
                        _continue = this.cells[k][jj] != null && this.cells[k][jj].opaque && !visited[k][jj];
                        if (!_continue) break;
                    }
                    if (!_continue) break;
                    jj++;
                }

                for (int _i = i; _i < ii; _i++)
                    for (int _j = j; _j < jj; _j++)
                        visited[_i][_j] = true;
                Rectangle rectangle = new Rectangle(
                        i * this.cellWidth, j * this.cellHeight,
                        (ii - i) * this.cellWidth, (jj - j) * this.cellHeight
                );
                this.segments.addAll(segmentsFromRectangle(rectangle));
            }
        }
    }
}
