package MapEditor.Map;

import Graphics.Fonts;
import Graphics.Textures;
import Globals.Globals;
import MapEditor.UI.IconChoiceWindow;
import MapEditor.UI.LightSourceWindow;
import MapEditor.UI.SliderWindow;
import Utils.Input;
import Utils.Utils;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static Utils.Visibility.*;

public class EditorMap {
    public enum Tool {
        FILL, BRUSH
    }

    public enum EditorMode {
        CELLS, LIGHTS
    }

    private List<Segment> segments;
    private int width, height;
    private EditorCell[][] cells;
    private List<LightSource> lightSources;
    private float cellWidth, cellHeight;
    public int playerStartPositionX, playerStartPositionY;
    public float playerWidth, playerHeight;
    public float gravity;
    public float dashDistance;
    public float speed;
    public float playerViewRadius;
    public float playerBlindViewRadius;
    public float jumpForce;
    public float extraJumpForce;

    private float xDrawOffset, yDrawOffset;
    private EditorMode editorMode;

    private static CircleShape circleShape;
    private static RenderTexture partialShadeTexture;
    private static RenderTexture completeShadeTexture;
    private RenderTexture shadeTexture;

    private LightSourceWindow lightSourceWindow;
    private IconChoiceWindow tileChoiceWindow;
    private IconChoiceWindow toolChoiceWindow;
    private IconChoiceWindow modeChoiceWindow;
    private SliderWindow jumpPadWindow;

    private Tool selectedTool;
    private boolean placingPlayer;
    private EditorCell.Type selectedCellType;

    private static Text playerStartText;

    private boolean movingLight;
    private LightSource selectedLightSource;

    private float anchorXDrawOffset, anchorYDrawOffset, anchorMousePosX, anchorMousePosY;

    public EditorMap(int width, int height, float cellWidth, float cellHeight) throws Exception {
        this.width = width;
        this.height = height;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.xDrawOffset = -(Globals.SCREEN_WIDTH - this.width * this.cellWidth) / 2;
        this.yDrawOffset = -(Globals.SCREEN_HEIGHT - this.height * this.cellHeight) / 2;

        this.playerStartPositionX = 5;
        this.playerStartPositionY = 5;
        this.playerWidth = 38;
        this.playerHeight = 38;

        this.gravity = 1.25f;
        this.dashDistance = 5;
        this.speed = 13.0f;
        this.playerViewRadius = 150;
        this.playerBlindViewRadius = 80;
        this.jumpForce = 3.4f;
        this.extraJumpForce = 2.4f;

        this.cells = new EditorCell[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.cells[i][j] = new EditorCell(i, j);
            }
        }

        circleShape = new CircleShape();
        partialShadeTexture = new RenderTexture();
        partialShadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));
        completeShadeTexture = new RenderTexture();
        completeShadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));
        this.shadeTexture = new RenderTexture();
        this.shadeTexture.create((int) (this.width * this.cellWidth), (int) (this.height * this.cellHeight));
        this.lightSources = new ArrayList<>();

        playerStartText = new Text();
        playerStartText.setCharacterSize(30);
        playerStartText.setFont(Fonts.getFont("Monospace"));
        playerStartText.setColor(Color.BLACK);
        playerStartText.setString("P");
        playerStartText.setStyle(TextStyle.BOLD);

        this.setupWindows();

        this.movingLight = false;
        this.selectedLightSource = null;

        this.changeEditorMode(EditorMode.CELLS);
    }

    private void setupWindows() {
        this.tileChoiceWindow = new IconChoiceWindow(0, Arrays.asList(
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 0),
                        () -> this.setSelectedTool(EditorCell.Type.NONE),
                        "None"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 1),
                        () -> this.setSelectedTool(EditorCell.Type.WALLS),
                        "Walls"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 2),
                        () -> this.setSelectedTool(EditorCell.Type.WATER),
                        "Water"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 3),
                        () -> this.setSelectedTool(EditorCell.Type.LAVA),
                        "Lava"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 4),
                        () -> this.setSelectedTool(EditorCell.Type.JUMP_PAD),
                        "Jump pad"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 5),
                        () -> this.setSelectedTool(EditorCell.Type.GRAVITY_PAD),
                        "Gravity pad"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(0, 6),
                        () -> this.placingPlayer = true,
                        "Player"
                )
        ));
        this.tileChoiceWindow.run(1);

        this.toolChoiceWindow = new IconChoiceWindow(0, Arrays.asList(
                new IconChoiceWindow.Button(
                        Textures.getIcon(1, 1),
                        () -> this.selectedTool = Tool.BRUSH,
                        "Brush"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(1, 2),
                        () -> this.selectedTool = Tool.FILL,
                        "Fill"
                )
        ));
        this.toolChoiceWindow.run(0);

        this.modeChoiceWindow = new IconChoiceWindow(0, Arrays.asList(
                new IconChoiceWindow.Button(
                        Textures.getIcon(1, 5),
                        () -> this.changeEditorMode(EditorMode.CELLS),
                        "Cells"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(1, 5),
                        () -> this.changeEditorMode(EditorMode.LIGHTS),
                        "Lights"
                ),
                new IconChoiceWindow.Button(
                        Textures.getIcon(1, 4),
                        this::save,
                        "Save"
                )
        ));
        this.modeChoiceWindow.run(0);

        this.jumpPadWindow = new SliderWindow(100, Collections.singletonList(
                new float[]{1, 15}
        ));

        this.tileChoiceWindow.setTopLeftPosition(0, 0);
        this.toolChoiceWindow.placeRight(this.tileChoiceWindow);
        this.modeChoiceWindow.setTopRightPosition((int) Globals.SCREEN_WIDTH, 0);

        this.lightSourceWindow = new LightSourceWindow();
        this.lightSourceWindow.setTopLeftPosition(0, 0);
    }

    private void setSelectedTool(EditorCell.Type type) {
        this.selectedCellType = type;
        this.placingPlayer = false;

        if (this.selectedCellType == EditorCell.Type.JUMP_PAD) {
            this.jumpPadWindow.placeBelow(this.tileChoiceWindow);
            this.jumpPadWindow.alignLeft(this.tileChoiceWindow);
        }
    }

    public void update() {
        this.modeChoiceWindow.update();
        Vector2f mousePos = Input.getCurrentMousePositionF();
        if (Input.isRightMousePressedOnce()) {
            this.anchorMousePosX = mousePos.x;
            this.anchorMousePosY = mousePos.y;
            this.anchorXDrawOffset = this.xDrawOffset;
            this.anchorYDrawOffset = this.yDrawOffset;
        }
        if (Input.isRightMousePressed()) {
            this.xDrawOffset = Utils.min(
                    this.width * this.cellWidth - Globals.SCREEN_WIDTH / 2,
                    Utils.max(
                            -Globals.SCREEN_WIDTH / 2,
                            this.anchorMousePosX - mousePos.x + this.anchorXDrawOffset
                    )
            );
            this.yDrawOffset = Utils.min(
                    this.height * this.cellHeight - Globals.SCREEN_HEIGHT / 2,
                    Utils.max(
                            -Globals.SCREEN_HEIGHT / 2,
                            this.anchorMousePosY - mousePos.y + this.anchorYDrawOffset
                    )
            );
        }

        switch (this.editorMode) {
            case CELLS:
                int i = (int) ((mousePos.x + this.xDrawOffset) / this.cellWidth);
                int j = (int) ((mousePos.y + this.yDrawOffset) / this.cellHeight);
                this.tileChoiceWindow.update();
                this.toolChoiceWindow.update();
                if (this.selectedCellType == EditorCell.Type.JUMP_PAD) this.jumpPadWindow.update();
                if (Utils.inBounds(i, 0, this.width) && Utils.inBounds(j, 0, this.height)) {
                    if (this.placingPlayer) {
                        if (Input.isLeftMousePressedOnce()) {
                            this.playerStartPositionX = i;
                            this.playerStartPositionY = j;
                        }
                    } else {
                        switch (this.selectedTool) {
                            case FILL:
                                EditorCell.Type type = this.cells[i][j].type;
                                if (this.selectedCellType != type && Input.isLeftMousePressedOnce()) {
                                    this.fill(i, j, this.selectedCellType);
                                }
                                break;
                            case BRUSH:
                                if (Input.isLeftMousePressed()) {
                                    this.setCell(i, j, this.selectedCellType);
                                }
                                break;
                        }
                    }
                }
                break;
            case LIGHTS:
                if (!this.movingLight && this.lightSourceWindow.update()) {
                    if (this.selectedLightSource != null) {
                        this.selectedLightSource.radius = this.lightSourceWindow.getRadius();
                        this.selectedLightSource.color = this.lightSourceWindow.getColor();
                    }
                } else {
                    if (this.selectedLightSource != null && Input.isKeyPressed(Keyboard.Key.DELETE)) {
                        this.lightSources.remove(this.selectedLightSource);
                        this.selectedLightSource = null;
                    }
                    float x = Utils.min(this.width * this.cellWidth, Utils.max(0.01f, mousePos.x + xDrawOffset));
                    float y = Utils.min(this.height * this.cellHeight, Utils.max(0.01f, mousePos.y + yDrawOffset));

                    if (Input.isLeftMousePressedOnce()) {
                        LightSource hoverLightSource = this.selectLightSource(mousePos.x, mousePos.y);
                        if (hoverLightSource != null) {
                            this.selectedLightSource = hoverLightSource;
                            this.lightSourceWindow.setLightSource(this.selectedLightSource);
                        } else if (this.selectedLightSource == null) {
                            if (
                                    Utils.inBounds(mousePos.x + this.xDrawOffset, 0f, this.width * this.cellWidth)
                                            &&
                                            Utils.inBounds(mousePos.y + this.yDrawOffset, 0f, this.height * this.cellHeight)
                            ) {
                                LightSource newLightSource = new LightSource(new Point(x, y), 100, Color.WHITE);
                                this.lightSourceWindow.setLightSource(newLightSource);
                                this.selectedLightSource = newLightSource;
                                this.lightSources.add(this.selectedLightSource);
                            }
                        } else {
                            this.selectedLightSource = null;
                        }
                    } else if (!Input.isLeftMousePressed()) {
                        this.movingLight = false;
                    }
                    if (this.selectedLightSource != null && Input.isLeftMousePressed()) {
                        this.movingLight = true;
                        if (Input.isKeyPressed(Keyboard.Key.LSHIFT) || Input.isKeyPressed(Keyboard.Key.RSHIFT)) {
                            x = Utils.min(
                                    2 * this.width - 2,
                                    (int) (2 * x / this.cellWidth)
                            ) / 2f * this.cellWidth + this.cellWidth / 2;
                            y = Utils.min(
                                    2 * this.height - 2,
                                    (int) (2 * y / this.cellHeight)
                            ) /2f * this.cellHeight + this.cellHeight / 2;
                        } else {
                            x = Utils.min(this.width * this.cellWidth - 0.01f, x);
                            y = Utils.min(this.height * this.cellHeight - 0.01f, y);
                        }
                        this.selectedLightSource.source.x = x;
                        this.selectedLightSource.source.y = y;
                    }
                }

                break;
        }
    }

    private void setCell(int i, int j, EditorCell.Type selectedCellType) {
        this.cells[i][j].type = selectedCellType;
        switch (this.selectedCellType) {
            case JUMP_PAD:
                this.cells[i][j].data = new EditorCell.JumpPadData((int) this.jumpPadWindow.getValue(0) + 0.4f);
                break;
        }
    }

    public void draw(List<RenderTexture> gameLayers, List<RenderTexture> interfaceLayers, RenderTexture shadeLayer) {
        int iStart = Utils.max(0, (int) (this.xDrawOffset / this.cellWidth));
        int jStart = Utils.max(0, (int) (this.yDrawOffset / this.cellHeight));
        for (int i = 0; i + iStart < this.width && i <= (int) (Globals.SCREEN_WIDTH / this.cellWidth); i++) {
            for (int j = 0; j + jStart < this.height && j <= (int) (Globals.SCREEN_HEIGHT / this.cellHeight); j++) {
                this.cells[i + iStart][j + jStart].draw(
                        gameLayers,
                        this.cellWidth, this.cellHeight,
                        this.xDrawOffset, this.yDrawOffset
                );
            }
        }

        float w = playerStartText.getLocalBounds().width;
        float h = playerStartText.getLocalBounds().height;
        float ratio = Utils.min(this.cellWidth / w, this.cellHeight / h) / 2;
        playerStartText.setOrigin(new Vector2f(w / 2, h / 2));
        playerStartText.setScale(ratio, ratio);
        playerStartText.setPosition(new Vector2f(
                this.playerStartPositionX * this.cellWidth - xDrawOffset + this.cellWidth / 2,
                this.playerStartPositionY * this.cellHeight - yDrawOffset + this.cellHeight / 2
        ));
        gameLayers.get(9).draw(playerStartText);

        this.modeChoiceWindow.draw(interfaceLayers);
        switch (this.editorMode) {
            case CELLS:
                shadeLayer.clear(Color.WHITE);
                this.tileChoiceWindow.draw(interfaceLayers);
                this.toolChoiceWindow.draw(interfaceLayers);
                if (this.placingPlayer) break;
                switch (this.selectedCellType) {
                    case JUMP_PAD:
                        this.jumpPadWindow.draw(interfaceLayers);
                }
                break;
            case LIGHTS:
                if (this.selectedLightSource != null) this.lightSourceWindow.draw(interfaceLayers);
                this.drawLightSourcesShadeTexture();
                this.drawShade(shadeLayer);
                break;
        }
    }

    public void drawShade(RenderTexture shadeLayer) {
        shadeLayer.clear(Color.BLACK);
        Sprite sprite = new Sprite();
        sprite.setTexture(this.shadeTexture.getTexture());
        sprite.setPosition(-this.xDrawOffset, -this.yDrawOffset);
        shadeLayer.draw(sprite);
    }

    private void fill(int i, int j, EditorCell.Type type) {
        EditorCell.Type initialType = this.cells[i][j].type;
        this.setCell(i, j, type);
        Arrays.asList(
                new int[]{-1, 0},
                new int[]{1, 0},
                new int[]{0, -1},
                new int[]{0, 1}
        ).forEach(coords -> {
            if (
                    Utils.inBounds(i + coords[0], 0, this.width) && Utils.inBounds(j + coords[1], 0, this.height) &&
                            this.cells[i + coords[0]][j + coords[1]].type == initialType
            ) {
                fill(i + coords[0], j + coords[1], type);
            }
        });
    }

    private LightSource selectLightSource(float x, float y) {
        return this.lightSources.stream().filter(lightSource -> {
            float dx = lightSource.source.x - (x + this.xDrawOffset);
            float dy = lightSource.source.y - (y + this.yDrawOffset);
            return (float) Math.sqrt(dx * dx + dy * dy) < Utils.min(this.cellWidth, this.cellHeight) / 2;
        }).findFirst().orElse(null);
    }

    private void changeMapSize(int width, int height) {
        EditorCell[][] cells = new EditorCell[width][height];
        for (int i = 0; i < this.width && i < width; i++) {
            for (int j = 0; j < this.height && j < height; j++) {
                cells[i][j] = this.cells[i][j];
            }
        }
        this.cells = cells;
    }

    private void save() {
        RawMap rawMap = new RawMap();
        rawMap.width = this.width;
        rawMap.height = this.height;
        rawMap.cellWidth = this.cellWidth;
        rawMap.cellHeight = this.cellHeight;
        rawMap.playerStartPositionX = this.playerStartPositionX;
        rawMap.playerStartPositionY = this.playerStartPositionY;
        rawMap.playerWidth = this.playerWidth;
        rawMap.playerHeight = this.playerHeight;
        rawMap.gravity = this.gravity;
        rawMap.dashDistance = this.dashDistance;
        rawMap.speed = this.speed;
        rawMap.playerViewRadius = this.playerViewRadius;
        rawMap.playerBlindViewRadius = this.playerBlindViewRadius;
        rawMap.jumpForce = this.jumpForce;
        rawMap.extraJumpForce = this.extraJumpForce;
        rawMap.mapElements = this.calculateMapElements();

        try {
            FileWriter fileWriter = new FileWriter(String.valueOf(Paths.get("out/test.json")));
            rawMap.toJSON().writeJSONString(fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeEditorMode(EditorMode editorMode) {
        this.editorMode = editorMode;
        switch (editorMode) {
            case CELLS:
                break;
            case LIGHTS:
                break;
        }
    }

    private void drawLightSourcesShadeTexture() {
        this.calculateSegments();
        this.shadeTexture.clear(Color.BLACK);
        this.lightSources.forEach(lightSource -> {
            calculateVisibility(lightSource, loadMap(this.segments, lightSource.source));
            lightSource.currentRadius = lightSource.radius;
            completeShadeTexture.clear(Color.BLACK);
            drawFieldOfView(partialShadeTexture, completeShadeTexture, lightSource, lightSource.color);
            drawRangeView(partialShadeTexture, completeShadeTexture, lightSource);
            completeShadeTexture.display();
            completeShadeTexture.setSmooth(true);
            this.shadeTexture.draw(new Sprite(completeShadeTexture.getTexture()), new RenderStates(BlendMode.ADD));
        });
        this.lightSources.forEach(lightSource -> {
            float circleRadius = Utils.min(this.cellWidth, this.cellHeight) / 2f;
            circleShape.setRadius(circleRadius);
            circleShape.setOrigin(circleRadius, circleRadius);
            circleShape.setFillColor(lightSource.color);
            circleShape.setPosition(lightSource.source.x, lightSource.source.y);
            if (this.selectedLightSource != lightSource) {
                circleShape.setOutlineThickness(1);
                circleShape.setOutlineColor(new Color(Color.BLACK, 128));
            } else {
                circleShape.setOutlineThickness(3);
                circleShape.setOutlineColor(new Color(Color.BLACK, 255));
            }
            this.shadeTexture.draw(circleShape);
        });
        this.shadeTexture.display();
    }

    private List<MapElement> calculateMapElements() {
        List<MapElement> mapElements = new ArrayList<>();

        this.calculateRectangles(EditorCell.Type.WALLS).forEach(rectangle -> mapElements.add(new MapElement.WallsElement(
                (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height
        )));
        this.calculateRectangles(EditorCell.Type.WATER).forEach(rectangle -> mapElements.add(new MapElement.WaterElement(
                (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height
        )));
        this.calculateRectangles(EditorCell.Type.LAVA).forEach(rectangle -> mapElements.add(new MapElement.Lavaelement(
                (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height
        )));
        this.lightSources.forEach(lightSource -> mapElements.add(new MapElement.LightSourceElement(
                lightSource.source.x / this.cellWidth, lightSource.source.y / this.cellHeight, lightSource.radius, lightSource.color
        )));
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                switch (this.cells[i][j].type) {
                    case JUMP_PAD:
                        mapElements.add(new MapElement.JumpPadElement(
                                i, j,
                                ((EditorCell.JumpPadData) this.cells[i][j].data).jumpForce
                        ));
                        break;
                    case GRAVITY_PAD:
                        mapElements.add(new MapElement.GravityPadElement(i, j));
                        break;
                }
            }
        }

        return mapElements;
    }

    private List<Rectangle> calculateRectangles(EditorCell.Type type) {
        List<Rectangle> rectangles = new ArrayList<>();
        boolean[][] visited = new boolean[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                if (visited[i][j] || this.cells[i][j].type != type) continue;

                int ii = i;
                while (ii < this.width && this.cells[ii][j].type == type) ii++;

                int jj = j;
                boolean _continue = true;
                while (jj < this.height) {
                    for (int k = i; k < ii; k++) {
                        _continue = this.cells[k][jj].type == type && !visited[k][jj];
                        if (!_continue) break;
                    }
                    if (!_continue) break;
                    jj++;
                }

                for (int _i = i; _i < ii; _i++)
                    for (int _j = j; _j < jj; _j++)
                        visited[_i][_j] = true;
                Rectangle rectangle = new Rectangle(
                        i, j,
                        (ii - i), (jj - j)
                );
                rectangles.add(rectangle);
            }
        }
        return rectangles;
    }

    private void calculateSegments() {
        List<Rectangle> rectangles = calculateRectangles(EditorCell.Type.WALLS);
        rectangles.forEach(rectangle -> {
            rectangle.x *= this.cellWidth;
            rectangle.y *= this.cellHeight;
            rectangle.width *= this.cellWidth;
            rectangle.height *= this.cellHeight;
        });
        rectangles.add(new Rectangle(
                0, 0,
                this.width * this.cellWidth, this.height * this.cellHeight
        ));
        this.segments = new ArrayList<>();
        rectangles.forEach(rectangle -> this.segments.addAll(segmentsFromRectangle(rectangle)));
    }
}
