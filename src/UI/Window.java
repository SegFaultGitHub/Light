package UI;

import Graphics.Textures;
import Utils.Utils;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Sprite;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas VENNER on 23/07/2016.
 */
public abstract class Window {
    protected Color windowColor;
    protected int x;
    protected int y;
    public int width;
    public int height;

    protected int offset;

    protected static final int CONTENT_OFFSET = 7;

    public Window(Color windowColor, int offset) {
        this.windowColor = windowColor;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.offset = offset;
    }

    protected Window(Color windowColor, int offset, int x, int y, int width, int height) {
        this.windowColor = windowColor;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.offset = offset;
    }

    // <editor-fold desc="Placement methods">
    public int topBound() {
        return this.y;
    }
    public int bottomBound() {
        return this.y + this.height;
    }
    public int leftBound() {
        return this.x;
    }
    public int rightBound() {
        return this.x + this.width;
    }
    public static int topBound(Window... windows) {
        return Utils.min(Arrays.stream(windows).map(window -> window.topBound()).collect(Collectors.toList()));
    }
    public static int bottomBound(Window... windows) {
        return Utils.max(Arrays.stream(windows).map(window -> window.bottomBound()).collect(Collectors.toList()));
    }
    public static int leftBound(Window... windows) {
        return Utils.min(Arrays.stream(windows).map(window -> window.leftBound()).collect(Collectors.toList()));
    }
    public static int rightBound(Window... windows) {
        return Utils.max(Arrays.stream(windows).map(window -> window.rightBound()).collect(Collectors.toList()));
    }
    public static int topBound(Collection<Window> windows) {
        List<Integer> bounds = windows.stream().map(window -> window.topBound()).collect(Collectors.toList());
        return Utils.min(bounds);
    }
    public static int bottomBound(Collection<Window> windows) {
        List<Integer> bounds = windows.stream().map(window -> window.bottomBound()).collect(Collectors.toList());
        return Utils.max(bounds);
    }
    public static int leftBound(Collection<Window> windows) {
        List<Integer> bounds = windows.stream().map(window -> window.leftBound()).collect(Collectors.toList());
        return Utils.min(bounds);
    }
    public static int rightBound(Collection<Window> windows) {
        List<Integer> bounds = windows.stream().map(window -> window.rightBound()).collect(Collectors.toList());
        return Utils.max(bounds);
    }

    public static int width(Collection<Window> windows) {
        return Window.rightBound(windows) - Window.leftBound(windows);
    }
    public static int height(Collection<Window> windows) {
        return Window.bottomBound(windows) - Window.topBound(windows);
    }

    public void setTop(int y) {
        this.y = y;
    }
    public void setBottom(int y) {
        this.setTop(y - this.height);
    }
    public void setLeft(int x) {
        this.x = x;
    }
    public void setRight(int x) {
        this.setLeft(x - this.width);
    }
    public void setTop(int y, int screenWidth, int screenHeight) {
        this.setTop(y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setBottom(int y, int screenWidth, int screenHeight) {
        this.setBottom(y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setLeft(int x, int screenWidth, int screenHeight) {
        this.setLeft(x);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setRight(int x, int screenWidth, int screenHeight) {
        this.setRight(x);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setXY(int x, int y) {
        this.setLeft(x);
        this.setTop(y);
    }

    public void setTopLeftPosition(int x, int y, int screenWidth, int screenHeight) {
        this.setXY(x,  y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setTopRightPosition(int x, int y, int screenWidth, int screenHeight) {
        this.setXY(x - this.width, y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setBottomLeftPosition(int x, int y, int screenWidth, int screenHeight) {
        this.setXY(x, y - this.height);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setBottomRightPosition(int x, int y, int screenWidth, int screenHeight) {
        this.setXY(x - this.width, y - this.height);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setCenterPosition(int x, int y, int screenWidth, int screenHeight) {
        this.setXY(x - this.width / 2, y - this.height / 2);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void setTopLeftPosition(int x, int y) {
        this.setXY(x,  y);
    }
    public void setTopRightPosition(int x, int y) {
        this.setXY(x - this.width, y);
    }
    public void setBottomLeftPosition(int x, int y) {
        this.setXY(x, y - this.height);
    }
    public void setBottomRightPosition(int x, int y) {
        this.setXY(x - this.width, y - this.height);
    }
    public void setCenterPosition(int x, int y) {
        this.setXY(x - this.width / 2, y - this.height / 2);
    }
//    public static void setTopLeftPosition(Collection<Window> windows, int x, int y) {
//        int
//        this.setXY(x,  y);
//    }
//    public static void setTopRightPosition(Collection<Window> windows, int x, int y) {
//        this.setXY(x - width, y);
//    }
//    public static void setBottomLeftPosition(Collection<Window> windows, int x, int y) {
//        this.setXY(x, y - height);
//    }
//    public static void setBottomRightPosition(Collection<Window> windows, int x, int y) {
//        this.setXY(x - width, y - height);
//    }
//    public static void setCenterPosition(Collection<Window> windows, int x, int y) {
//        this.setXY(x - width / 2, y - height / 2);
//    }

    public void placeAbove(Window window, int screenWidth, int screenHeight) {
        this.placeAbove(window);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void placeBelow(Window window, int screenWidth, int screenHeight) {
        this.placeBelow(window);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void placeLeft(Window window, int screenWidth, int screenHeight) {
        this.placeLeft(window);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void placeRight(Window window, int screenWidth, int screenHeight) {
        this.placeRight(window);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void placeAbove(Window window) {
        this.setBottom(window.topBound());
        this.moveUp(1);
    }
    public void placeBelow(Window window) {
        this.setTop(window.bottomBound());
        this.moveDown(1);
    }
    public void placeLeft(Window window) {
        this.setRight(window.rightBound());
        this.moveLeft(1);
    }
    public void placeRight(Window window) {
        this.setLeft(window.rightBound());
        this.moveRight(1);
    }
    public void placeAbove(Collection<Window> windows) {
        this.setBottom(Window.topBound(windows));
        this.moveUp(1);
    }
    public void placeBelow(Collection<Window> windows) {
        this.setTop(Window.bottomBound(windows));
        this.moveDown(1);
    }
    public void placeLeft(Collection<Window> windows) {
        this.setRight(Window.leftBound(windows));
        this.moveLeft(1);
    }
    public void placeRight(Collection<Window> windows) {
        this.setLeft(Window.rightBound(windows));
        this.moveRight(1);
    }

    public void alignTop(Window window, int screenWidth, int screenHeight) {
        this.setTop(window.y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void alignBottom(Window window, int screenWidth, int screenHeight) {
        this.setTop(window.y + window.height - this.height);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void alignLeft(Window window, int screenWidth, int screenHeight) {
        this.setLeft(window.x);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void alignRight(Window window, int screenWidth, int screenHeight) {
        this.setLeft(window.x + window.width - this.width);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void alignTop(Window window) {
        this.setTop(window.topBound());
    }
    public void alignBottom(Window window) {
        this.setBottom(window.bottomBound());
    }
    public void alignLeft(Window window) {
        this.setLeft(window.leftBound());
    }
    public void alignRight(Window window) {
        this.setRight(window.rightBound());
    }
    public void alignTop(Collection<Window> windows) {
        this.setTop(Window.topBound(windows));
    }
    public void alignBottom(Collection<Window> windows) {
        this.setBottom(Window.bottomBound(windows));
    }
    public void alignLeft(Collection<Window> windows) {
        this.setLeft(Window.leftBound(windows));
    }
    public void alignRight(Collection<Window> windows) {
        this.setRight(Window.rightBound(windows));
    }

    public void moveUp(int y, int screenWidth, int screenHeight) {
        this.setTop(this.topBound() - y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void moveDown(int y, int screenWidth, int screenHeight) {
        this.setTop(this.topBound() + y);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void moveLeft(int x, int screenWidth, int screenHeight) {
        this.setLeft(this.leftBound() - x);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void moveRight(int x, int screenWidth, int screenHeight) {
        this.setLeft(this.leftBound() + x);
        this.adjustPosition(screenWidth, screenHeight);
    }
    public void moveUp(int y) {
        this.setTop(this.y - y);
    }
    public void moveDown(int y) {
        this.setTop(this.y + y);
    }
    public void moveLeft(int x) {
        this.setLeft(this.x - x);
    }
    public void moveRight(int x) {
        this.setLeft(this.x + x);
    }
    public static void moveUp(int y, Window... windows) {
        Arrays.stream(windows).forEach(window -> window.moveUp(y));
    }
    public static void moveDown(int y, Window... windows) {
        Arrays.stream(windows).forEach(window -> window.moveDown(y));
    }
    public static void moveLeft(int x, Window... windows) {
        Arrays.stream(windows).forEach(window -> window.moveLeft(x));
    }
    public static void moveRight(int x, Window... windows) {
        Arrays.stream(windows).forEach(window -> window.moveRight(x));
    }
    public static void moveUp(int y, Collection<Window> windows) {
        windows.forEach(window -> window.moveUp(y));
    }
    public static void moveDown(int y, Collection<Window> windows) {
        windows.forEach(window -> window.moveDown(y));
    }
    public static void moveLeft(int x, Collection<Window> windows) {
        windows.forEach(window -> window.moveLeft(x));
    }
    public static void moveRight(int x, Collection<Window> windows) {
        windows.forEach(window -> window.moveRight(x));
    }

    public static void placeHorizontally(HashMap<String, Window> windows, List<List<String>> placement, int x, int y) {
        List<List<Window>> arr = placement.stream()
                .map(l -> l.stream().map(windows::get).filter(Objects::nonNull).collect(Collectors.toList()))
                .filter(l -> l.stream().anyMatch(Objects::nonNull))
                .collect(Collectors.toList());

        arr.get(0).get(0).setXY(x, y);
        rearrangeHorizontally(windows, placement);
    }
    public static void placeVertically(HashMap<String, Window> windows, List<List<String>> placement, int x, int y) {
        List<List<Window>> arr = placement.stream()
                .map(l -> l.stream().map(windows::get).filter(Objects::nonNull).collect(Collectors.toList()))
                .filter(l -> l.stream().anyMatch(Objects::nonNull))
                .collect(Collectors.toList());

        arr.get(0).get(0).setXY(x, y);
        rearrangeVertically(windows, placement);
    }

    public static void rearrangeHorizontally(HashMap<String, Window> windows, List<List<String>> placement) {
        List<List<Window>> arr = placement.stream()
                .map(l -> l.stream().map(windows::get).filter(Objects::nonNull).collect(Collectors.toList()))
                .filter(l -> l.stream().anyMatch(Objects::nonNull))
                .collect(Collectors.toList());

        int rows = arr.size();
        for (int j = 0; j < rows; j++) {
            int columns = arr.get(j).size();
            if (j > 0) {
                Window windowAbove = arr.get(j - 1).get(0);
                Window currentWindow = arr.get(j).get(0);
                currentWindow.placeBelow(arr.get(j - 1));
                currentWindow.alignLeft(windowAbove);
            }
            for (int i = 1; i < columns; i++) {
                Window windowLeft = arr.get(j).get(i - 1);
                Window currentWindow = arr.get(j).get(i);
                currentWindow.placeRight(windowLeft);
                currentWindow.alignTop(windowLeft);
            }
        }
    }
    public static void rearrangeVertically(HashMap<String, Window> windows, List<List<String>> placement) {
        List<List<Window>> arr = placement.stream()
                .map(l -> l.stream().map(windows::get).filter(Objects::nonNull).collect(Collectors.toList()))
                .filter(l -> l.stream().anyMatch(Objects::nonNull))
                .collect(Collectors.toList());

        int rows = arr.size();
        for (int j = 0; j < rows; j++) {
            int columns = arr.get(j).size();
            if (j > 0) {
                Window windowAbove = arr.get(j - 1).get(0);
                Window currentWindow = arr.get(j).get(0);
                currentWindow.placeRight(arr.get(j - 1));
                currentWindow.alignTop(windowAbove);
            }
            for (int i = 1; i < columns; i++) {
                Window windowLeft = arr.get(j).get(i - 1);
                Window currentWindow = arr.get(j).get(i);
                currentWindow.placeBelow(windowLeft);
                currentWindow.alignLeft(windowLeft);
            }
        }
    }

    protected void adjustPosition(int screenWidth, int screenHeight) {
        if (this.topBound() < 0) {
            this.moveDown(-this.topBound());
        }
        if (this.bottomBound() >= screenHeight) {
            this.moveUp(this.bottomBound() - screenHeight);
        }
        if (this.leftBound() < 0) {
            this.moveRight(-this.leftBound());
        }
        if (rightBound() >= screenWidth) {
            this.moveLeft(this.rightBound() - screenWidth);
        }
    }
    // </editor-fold>

    public abstract boolean update();

    public void draw(List<RenderTexture> layers) {
        RenderTexture target = layers.get(this.offset);
        Sprite sprite = new Sprite();
        sprite.setTexture(Textures.windowskin);

        sprite.setTextureRect(new IntRect(0, 0, 64, 64));
        sprite.setPosition(this.x + 2, this.y + 2);
        sprite.setScale(((float) this.width - 4f) / 64f, ((float) this.height - 4f) / 64f);
        sprite.setColor(this.windowColor);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(71, 0, 1, 7));
        sprite.setPosition(this.x + 7, this.y);
        sprite.setScale(this.width - 14, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(64, 7, 7, 1));
        sprite.setPosition(this.x, this.y + 7);
        sprite.setScale(1, this.height - 14);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(71, 57, 1, 7));
        sprite.setPosition(this.x + 7, this.y + this.height - 7);
        sprite.setScale(this.width - 14, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(121, 7, 7, 1));
        sprite.setPosition(this.x + this.width - 7, this.y + 7);
        sprite.setScale(1, this.height - 14);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(64, 0, 7, 7));
        sprite.setPosition(this.x, this.y);
        sprite.setScale(1, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(121, 0, 7, 7));
        sprite.setPosition(this.x + this.width - 7, this.y);
        sprite.setScale(1, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(64, 57, 7, 7));
        sprite.setPosition(this.x, this.y + this.height - 7);
        sprite.setScale(1, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);

        sprite.setTextureRect(new IntRect(121, 57, 7, 7));
        sprite.setPosition(this.x + this.width - 7, this.y + this.height - 7);
        sprite.setScale(1, 1);
        sprite.setColor(Color.WHITE);
        target.draw(sprite);
    }
}
