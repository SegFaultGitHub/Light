package Utils;

import UI.Window;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Joystick;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.JoystickMoveEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class Input {
    private static Vector2i currentMousePosition;
    private static Vector2i previousMousePosition;
    private static boolean currentLeftMouseButtonPressed;
    private static boolean previousLeftMouseButtonPressed;
    private static boolean currentRightMouseButtonPressed;
    private static boolean previousRightMouseButtonPressed;
    private static int currentMouseWheel;
    private static int previousMouseWheel;

    private static final HashMap<Keyboard.Key, Boolean> currentKeys = new HashMap<>();
    private static final HashMap<Keyboard.Key, Boolean> previousKeys = new HashMap<>();
    private static final HashMap<Joystick.Axis, Float> joystickAxis = new HashMap<>();
    private static final HashMap<JoystickButton, Boolean> currentJoystickButtons = new HashMap<>();
    private static final HashMap<JoystickButton, Boolean> previousJoystickButtons = new HashMap<>();

    // Button mapping
    public enum JoystickButton {
        A, B, X, Y, LT, LB, RT, RB, LSB, RSB, BACK, START
    }
    private static HashMap<Integer, JoystickButton> joystickButtonsMapping = new HashMap() {
        {
            put(0, JoystickButton.A);
            put(1, JoystickButton.B);
            put(2, JoystickButton.X);
            put(3, JoystickButton.Y);
            put(4, JoystickButton.LB);
            put(5, JoystickButton.RB);
            put(6, JoystickButton.BACK);
            put(7, JoystickButton.START);
            put(8, JoystickButton.LSB);
            put(9, JoystickButton.RSB);
        }
    };

    public static void initialize() {
        currentLeftMouseButtonPressed = false;
        previousLeftMouseButtonPressed = false;
        currentRightMouseButtonPressed = false;
        previousRightMouseButtonPressed = false;
        currentMousePosition = new Vector2i(0, 0);
        previousMousePosition = new Vector2i(0, 0);
        currentMouseWheel = 0;
        previousMouseWheel = 0;
    }

    public static void update(RenderWindow window, Iterable<Event> events) {
        previousMouseWheel = currentMouseWheel;
        previousMousePosition = currentMousePosition;
        previousLeftMouseButtonPressed = currentLeftMouseButtonPressed;
        previousRightMouseButtonPressed = currentRightMouseButtonPressed;
        currentKeys.forEach(previousKeys::put);
        currentJoystickButtons.forEach(previousJoystickButtons::put);

        currentMouseWheel = 0;
        currentMousePosition = Mouse.getPosition(window);
        for (Event event : events) {
            if (event.type == Event.Type.MOUSE_WHEEL_MOVED) {
                currentMouseWheel = event.asMouseWheelEvent().delta;
            } else if (event.type == Event.Type.KEY_PRESSED) {
                currentKeys.put(event.asKeyEvent().key, true);
            } else if (event.type == Event.Type.KEY_RELEASED) {
                currentKeys.put(event.asKeyEvent().key, false);
            } else if (event.type == Event.Type.MOUSE_BUTTON_PRESSED) {
                if (event.asMouseButtonEvent().button == Mouse.Button.LEFT) {
                    currentLeftMouseButtonPressed = true;
                } else if (event.asMouseButtonEvent().button == Mouse.Button.RIGHT) {
                    currentRightMouseButtonPressed = true;
                }
            } else if (event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
                if (event.asMouseButtonEvent().button == Mouse.Button.LEFT) {
                    currentLeftMouseButtonPressed = false;
                } else if (event.asMouseButtonEvent().button == Mouse.Button.RIGHT) {
                    currentRightMouseButtonPressed = false;
                }
            }

            if (event.type == Event.Type.JOYSTICK_BUTTON_PRESSED) {
                currentJoystickButtons.put(joystickButtonsMapping.get(event.asJoystickButtonEvent().button), true);
            } else if (event.type == Event.Type.JOYSTICK_BUTTON_RELEASED) {
                currentJoystickButtons.put(joystickButtonsMapping.get(event.asJoystickButtonEvent().button), false);
            } else if (event.type == Event.Type.JOYSTICK_MOVED) {
                JoystickMoveEvent event_ = event.asJoystickMoveEvent();
                if (Math.abs(event_.position) < 10) {
                    if (event_.joyAxis == Joystick.Axis.Z) {
                        currentJoystickButtons.put(JoystickButton.LT, false);
                        currentJoystickButtons.put(JoystickButton.RT, false);
                    }
                    joystickAxis.put(event_.joyAxis, 0f);
                }
                else if (event_.joyAxis == Joystick.Axis.Z) {
                    if (event_.position > 0) currentJoystickButtons.put(JoystickButton.LT, true);
                    else currentJoystickButtons.put(JoystickButton.RT, true);
                } else joystickAxis.put(event_.joyAxis, event_.position);
            }
        }
    }

    public static String foo() {
        String result = "";
        for (Map.Entry<Joystick.Axis, Float> entry : joystickAxis.entrySet()) {
            result += entry.getKey();
            result += " : ";
            result += entry.getValue();
            result += "\n";
        }
        result += "---\n";
        for (Map.Entry<JoystickButton, Boolean> entry : currentJoystickButtons.entrySet()) {
            result += entry.getKey();
            result += " : ";
            result += entry.getValue();
            result += "\n";
        }
        return result;
    }

    public static Vector2i getCurrentMousePosition() {
        return currentMousePosition;
    }
    public static Vector2f getCurrentMousePositionF() {
        return new Vector2f(currentMousePosition.x, currentMousePosition.y);
    }
    public static Vector2i getCurrentMousePosition(Window window) {
        return Vector2i.sub(currentMousePosition, new Vector2i(window.leftBound(), window.topBound()));
    }

    public static boolean isKeyPressed(Keyboard.Key key) {
        return (currentKeys.get(key) != null && currentKeys.get(key));
    }
    public static boolean isKeyPressedOnce(Keyboard.Key key) {
        return isKeyPressed(key) && (previousKeys.get(key) == null || !previousKeys.get(key));
    }

    public static boolean isJoystickButtonPressed(JoystickButton button) {
        return (currentJoystickButtons.get(button) != null && currentJoystickButtons.get(button));
    }
    public static boolean isJoystickButtonPressedOnce(JoystickButton button) {
        return isJoystickButtonPressed(button) && (previousJoystickButtons.get(button) == null || !previousJoystickButtons.get(button));
    }
    public static boolean isJoystickButtonReleased(JoystickButton button) {
        return !isJoystickButtonPressed(button) && (previousJoystickButtons.get(button) != null && previousJoystickButtons.get(button));
    }
    public static float getJoystickAxis(Joystick.Axis axis) {
        Float result = joystickAxis.get(axis);
        return result == null ? 0 : result;
    }

    public static boolean isMouseInRect(int x, int y, int w, int h) {
        return currentMousePosition.x >= x && currentMousePosition.y >= y &&
                currentMousePosition.x < x + w && currentMousePosition.y < y + h;
    }
    public static boolean isMouseInRect(float x, float y, float w, float h) {
        return currentMousePosition.x >= x && currentMousePosition.y >= y &&
                currentMousePosition.x < x + w && currentMousePosition.y < y + h;
    }
    public static boolean isMouseInWindow(Window window) {
        return isMouseInRect(window.leftBound(), window.topBound(), window.width, window.height);
    }
    public static boolean isLeftMousePressed() {
        return currentLeftMouseButtonPressed;
    }
    public static boolean isLeftMousePressedOnce() {
        return currentLeftMouseButtonPressed && !previousLeftMouseButtonPressed;
    }
    public static boolean isLeftMouseReleased() {
        return !currentLeftMouseButtonPressed && previousLeftMouseButtonPressed;
    }
    public static boolean isRightMousePressed() {
        return currentRightMouseButtonPressed;
    }
    public static boolean isRightMousePressedOnce() {
        return currentRightMouseButtonPressed && !previousRightMouseButtonPressed;
    }
    public static boolean isRightMouseReleased() {
        return !currentRightMouseButtonPressed && previousRightMouseButtonPressed;
    }
    public static boolean isMouseReleased() {
        return !currentLeftMouseButtonPressed && previousLeftMouseButtonPressed;
    }
    public static boolean isMousePressedOnceIsRect(int x, int y, int w, int h) {
        return isLeftMousePressedOnce() && isMouseInRect(x, y, w, h);
    }
    public static boolean isLeftMousePressedIsRect(int x, int y, int w, int h) {
        return isLeftMousePressed() && isMouseInRect(x, y, w, h);
    }
    public static boolean isMouseScrollingDown() {
        return currentMouseWheel == -1;
    }
    public static boolean isMouseScrollingUp() {
        return currentMouseWheel == 1;
    }
}
