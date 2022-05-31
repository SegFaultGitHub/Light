package Player;

import Map.Map;
import Map.Cell;
import Particles.Particles;
import Utils.Input;
import Utils.Utils;
import Utils.Visibility;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Joystick;

import java.util.ArrayList;
import java.util.List;

import static Utils.Visibility.segmentsFromRectangle;

public class Player {
    public float x, y;
    public float width, height;
    public float speedX, speedY;

    private boolean grounded;
    private boolean jumping;
    private int dashFrames;
    private int extraDashes;
    private int extraJumps;

    private int DASH_DURATION = 10; // in frames
    private int EXTRA_DASHES = 0;
    private int EXTRA_JUMPS = 1;

    private List<float[]> collisionPoints;
    public List<Visibility.Segment> segments;

    private int[] lastCell;

    // Temp
    private static final RectangleShape shape = new RectangleShape();

    // Public

    public Player(Map map) {
        this.width = map.playerWidth;
        this.height = map.playerHeight;
        this.collisionPoints = new ArrayList<>();
        List<Float> xList = new ArrayList<>();
        xList.add(0f);
        xList.add(this.width);
        List<Float> yList = new ArrayList<>();
        yList.add(0f);
        yList.add(this.height);
        for (float _x = map.cellWidth; _x < this.width; _x += map.cellWidth) xList.add(_x);
        for (float _y = map.cellHeight; _y < this.height; _y += map.cellHeight) yList.add(_y);
        xList.forEach(x -> yList.forEach(y-> this.collisionPoints.add(new float[]{ x, y })));
        this.calculateSegments();
    }

    public void update(Map map) {
        int i = (int) ((this.x + this.width / 2) / map.cellWidth);
        int j = (int) ((this.y + this.height / 2) / map.cellHeight);
        Cell currentCell = map.getCell(i, j);
        if (currentCell != null && currentCell.getClass() == Cell.Water.class) {
            if (Input.isJoystickButtonPressed(Input.JoystickButton.A)) {
                if (Math.signum(this.speedY) == Math.signum(map.gravity)) this.speedY = 0;
                this.speedY -= map.gravity / 3f;
            } else {
                if (Math.signum(this.speedY) != Math.signum(map.gravity)) this.speedY = 0;
                this.speedY += map.gravity / 10f;
            }
            if (Math.abs(this.speedY) > Math.abs(map.gravity) * 1.5f) this.speedY = Math.signum(this.speedY) * 2;

            this.speedX = Input.getJoystickAxis(Joystick.Axis.X) / 100f * (map.speed / 1.5f);
        } else {
            this.speedY += map.gravity;
            if (Input.isJoystickButtonPressedOnce(Input.JoystickButton.A)) {
                this.jump(map);
            } else if (Input.isJoystickButtonReleased(Input.JoystickButton.A)) {
                if (this.jumping && Math.signum(this.speedY) != Math.signum(map.gravity)) this.speedY = 0;
            }
            this.speedX = Input.getJoystickAxis(Joystick.Axis.X) / 100f * map.speed;

            if (Input.isJoystickButtonPressed(Input.JoystickButton.LB)) {
                if (Input.isJoystickButtonPressedOnce(Input.JoystickButton.LB) && this.extraDashes <= this.EXTRA_DASHES) {
                    this.dashFrames = this.DASH_DURATION;
                    this.extraDashes++;
                }
                this.dash(map, -1);
            } else if (Input.isJoystickButtonReleased(Input.JoystickButton.LB)) {
                this.dashFrames = 0;
            }
            if (Input.isJoystickButtonPressed(Input.JoystickButton.RB)) {
                if (Input.isJoystickButtonPressedOnce(Input.JoystickButton.RB) && this.extraDashes <= this.EXTRA_DASHES) {
                    this.dashFrames = this.DASH_DURATION;
                    this.extraDashes++;
                }
                this.dash(map, 1);
            } else if (Input.isJoystickButtonReleased(Input.JoystickButton.RB)) {
                this.dashFrames = 0;
            }
        }


        if (moveXAndCollide(map, this.speedX)) {
            this.dashFrames = 0;
        } else if (this.grounded && Math.abs(this.speedX) > map.speed / 2) {
            this.generateParticles(map, Utils.getRandomInt(-3, 2));
        }
        if (moveYAndCollide(map, this.speedY)) {
            boolean previousGrounded = this.grounded;
            this.grounded = Math.signum(this.speedY) == Math.signum(map.gravity);
            if (this.grounded && !previousGrounded) {
                this.generateParticles(map, Utils.getRandomInt(3, 5));
                this.resetMovementAttributes();
            }
            this.speedY = 0;
        } else {
            this.grounded = false;
        }

        this.calculateSegments();
    }

    public void calculateSegments() {
        this.segments = segmentsFromRectangle(new Visibility.Rectangle(this.x, this.y, this.width, this.height));
    }

    private void jump(Map map) {
        if (!this.grounded && this.extraJumps < this.EXTRA_JUMPS) {
            this.speedY = map.extraJumpForce * -Math.signum(map.gravity);
            this.extraJumps++;
            this.dashFrames = 0;
        } else if (this.grounded) {
            this.generateParticles(map, Utils.getRandomInt(5, 7));
            this.speedY = map.jumpForce * -Math.signum(map.gravity);
            this.jumping = true;
            this.dashFrames = 0;
        }
    }

    private void generateParticles(Map map, int particleCount) {
        float particleJumpForce = map.getJumpForce(0.5f);
        for (int i = 0; i < particleCount; i++) {
            Particles.generateParticle(
                    Utils.getRandomFloat(this.width / 7, this.width / 5),
                    this.x + this.width / 2, this.y + (map.gravity > 0 ? this.height : 0),
                    Utils.getRandomFloat(-1.5f, 1.5f) * particleJumpForce,
                    map.gravity > 0 ? Utils.getRandomFloat(-1f, -0.75f) * particleJumpForce : Utils.getRandomFloat(0f, 0.1f) * particleJumpForce,
                    Color.BLACK
            );
        }
    }

    public void draw(List<RenderTexture> layers, float xDrawOffset, float yDrawOffset) {
//        shape.setFillColor(this.grounded ? Color.GREEN : Color.RED);
        shape.setFillColor(Color.BLACK);
        shape.setSize(new Vector2f(this.width + 2, this.height + 2));
        shape.setPosition(this.x - xDrawOffset - 1, this.y - yDrawOffset - 1);
        layers.get(4).draw(shape);
    }

    public void resetMovementAttributes() {
        this.extraJumps = 0;
        this.extraDashes = 0;
        this.jumping = false;
        this.speedY = 0;
    }

    public void setPositionOnMap(Map map, int i, int j) {
        this.x = (i + 0.5f) * map.cellWidth - this.width / 2;
        this.y = (j + 0.5f) * map.cellHeight - this.height / 2;
    }

    public void forceJump(Map map, float force, boolean allowDash, boolean allowExtraJump) {
        this.dashFrames = 0;
        if (!allowDash) this.extraDashes = this.EXTRA_DASHES + 1;
        if (!allowExtraJump) this.extraJumps = this.EXTRA_JUMPS;
        this.speedY = force * -Math.signum(map.gravity);
        this.jumping = false;
    }

    private void dash(Map map, int direction) {
        if (this.dashFrames > 0) {
            this.speedY = 0;
            this.speedX = direction * (map.dashDistance * map.cellWidth / this.DASH_DURATION);
            this.dashFrames--;
        }
    }

    public boolean moveXAndCollide(Map map, float x) {
        float direction = Math.signum(x);
        if (direction == 0) return false;

        boolean collide = false;
        while (Math.signum(x) == direction) {
            float stepX = Math.abs(x) > map.cellWidth ? map.cellWidth * direction : x;
            if (this.collision(this.x + stepX, this.y, map)) {
                x -= Math.abs(direction / 2) < Math.abs(x) ? direction / 2 : x;
                collide = true;
            } else {
                this.x += stepX;
                x -= map.cellWidth * direction;
            }

            this.applyCellEffect(map);
        }
        return collide;
    }

    public boolean moveYAndCollide(Map map, float y) {
        float direction = Math.signum(y);
        if (direction == 0) return false;

        boolean collide = false;
        while (Math.signum(y) == direction) {
            float stepY = Math.abs(y) > map.cellHeight ? map.cellHeight * direction : y;
            if (this.collision(this.x, this.y + stepY, map)) {
                y -= Math.abs(direction / 2) < Math.abs(y) ? direction / 2 : y;
                collide = true;
            } else {
                this.y += stepY;
                y -= map.cellHeight * direction;
            }

            this.applyCellEffect(map);
        }
        return collide;
    }

    private void applyCellEffect(Map map) {
        int i = (int) ((this.x + this.width / 2) / map.cellWidth);
        int j = (int) ((this.y + this.height / 2) / map.cellHeight);
        Cell currentCell = map.getCell(i, j);
        if (this.lastCell == null) this.lastCell = new int[]{ i, j };

        if (currentCell != null) currentCell.applyEffect(map, this);
        else applyDefaultEffect(map);

        if (this.lastCell[0] != i || this.lastCell[1] != j) {
            Cell lastCell = map.getCell(this.lastCell[0], this.lastCell[1]);
            if (lastCell != null) lastCell.disabled = false;
            this.lastCell = new int[]{ i, j };

            // Get into water
            if (
                    (currentCell != null && currentCell.getClass() == Cell.Water.class)
                            &&
                    (lastCell == null || lastCell.getClass() != Cell.Water.class)
            ) {
                this.resetMovementAttributes();
            }
            // Get out of water
            if (
                    (lastCell != null && lastCell.getClass() == Cell.Water.class)
                            &&
                    (currentCell == null || currentCell.getClass() != Cell.Water.class)
                            &&
                    Math.signum(this.speedY) != Math.signum(map.gravity)
            ) {
                this.forceJump(map, map.getJumpForce(1.2f), true, true);
            }
        }
    }

    private void applyDefaultEffect(Map map) {
        map.playerBlind = false;
    }


    // Private

    private boolean collision(float x, float y, Map map) {
        return this.collisionPoints.stream().anyMatch(coords -> map.collision(
                (int) ((coords[0] + x) / map.cellWidth),
                (int) ((coords[1] + y) / map.cellHeight)
        ));
    }

    public void die(Map map) {
        map.placePlayer(this);
    }
}
