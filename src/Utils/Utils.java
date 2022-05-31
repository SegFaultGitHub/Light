package Utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Utils {
    private static Random random = new Random();

    public static JSONObject readJSON(String path) throws Exception {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new FileReader(path));
    }

    public static String stringRepeat(String s, int n) {
        n = Math.max(0, n);
        return new String(new char[n]).replace("\0", s);
    }

    public static String reformatJSON(JSONObject jsonObject, int tab) {
        StringBuilder result = new StringBuilder(stringRepeat(" ", 4 * (tab)) + "{\n");
        boolean first = true;
        for (Object obj : jsonObject.keySet()) {
            if (!first) {
                result.append(",\n");
            }
            first = false;
            String key = obj.toString();
            Object value = jsonObject.get(key);
            result.append(stringRepeat(" ", 4 * (tab + 1))).append("\"").append(key).append("\" : ");
            if (value.getClass() == JSONObject.class) {
                result.append(reformatJSON((JSONObject) value, tab + 1));
            } else if (value.getClass() == JSONArray.class) {
                result.append("[");
                boolean first2 = true;
                for (Object obj2 : (JSONArray) value) {
                    if (!first2) {
                        result.append(", ");
                    }
                    first2 = false;
                    if (obj2.getClass() == JSONObject.class) {
                        result.append(reformatJSON((JSONObject) obj2, tab + 1));
                    } else if (obj2.getClass() == String.class) {
                        String value2 = (String) obj2;
                        result.append(stringRepeat(" ", 4 * (tab + 2))).append('"').append(value2).append('"');
                    }
                }
                result.append("\n").append(stringRepeat(" ", 4 * (tab + 1))).append("]");
            } else if (value.getClass() == String.class) {
                result.append("\"").append(value.toString()).append("\"");
            } else {
                result.append(value.toString());
            }
        }
        result.append("\n").append(stringRepeat(" ", tab * 4)).append("}");
        return result.toString();
    }

    public static <T extends Comparable> T constrain(T n, T min, T max) {
        if (n.compareTo(min) < 0) return min;
        if (n.compareTo(max) > 0) return max;
        return n;
    }

    public static <T extends Comparable> T max(T... args) {
        T max = args[0];
        for (T arg : args) {
            if (arg.compareTo(max) > 0) max = arg;
        }
        return max;
    }
    public static <T extends Comparable> T max(List<T> args) {
        T max = args.get(0);
        for (T arg : args) {
            if (arg.compareTo(max) > 0) max = arg;
        }
        return max;
    }

    public static <T extends Comparable> T min(T... args) {
        T min = args[0];
        for (T arg : args) {
            if (arg.compareTo(min) < 0) min = arg;
        }
        return min;
    }
    public static <T extends Comparable> T min(List<T> args) {
        T min = args.get(0);
        for (T arg : args) {
            if (arg.compareTo(min) < 0) min = arg;
        }
        return min;
    }

    // min included, max excluded
    public static <T extends Comparable> boolean inBounds(T n, T min, T max) {
        return n.compareTo(min) >= 0 && n.compareTo(max) < 0;
    }

    public static <T> String implode(T[] array, String s) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                result += s;
            }
            result += array[i].toString();
        }
        return result;
    }

    // Min included, max included
    public static int getRandomInt(int min, int max) {
        max += 1;
        if (min < max) return random.nextInt(max - min) + min;
        else return random.nextInt(min - max) + max;
    }

    public static float getRandomFloat(float min, float max) {
        if (min < max) return random.nextFloat() * (max - min) + min;
        else return random.nextFloat() * (min - max) + max;
    }

    public static String getRandomString(int n) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            chars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            chars.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            chars.add(c);
        }
        String result = "";
        for (int i = 0; i < n; i++) {

            result += chars.get(Math.abs(random.nextInt()) % chars.size());
        }
        return result;
    }

    public static <T> List<T> sampleArray(List<T> list, int count) {
        if (list.isEmpty()) return null;
        List<T> clone = new ArrayList<>(list);
        List<T> result = new ArrayList<>();
        for (int i = 0; i < count && clone.size() > 0; i++) {
            int index = getRandomInt(0, clone.size() - 1);
            result.add(clone.remove(index));
        }
        return result;
    }

    public static <T> List<T> sampleArray(T[] array, int count) {
        return sampleArray(Arrays.asList(array), count);
    }

    public static <T> T sampleArray(List<T> list) {
        if (list.isEmpty()) return null;
        return list.get(getRandomInt(0, list.size() - 1));
    }

    public static <T> T sampleArray(T[] array) {
        return sampleArray(Arrays.asList(array));
    }

    public static <T> T randomRange(List<Pair<Integer, T>> o) {
        int total = 0;
        for (Pair pair : o) {
            total += (int) pair.first;
        }
        int choice = getRandomInt(1, total);
        int index = 0;
        for (Pair pair : o) {
            index += (int) pair.first;
            if (choice <= index) return (T) pair.second;
        }

        return o.get(o.size() - 1).second;
    }

    public static boolean inTriangleFan(float sourceX, float sourceY, float directionX, float directionY, float radius, float range, float targetX, float targetY) {
        float targetDirectionX = targetX - sourceX;
        float targetDirectionY = targetY - sourceY;
        double distance = Math.sqrt(targetDirectionX * targetDirectionX + targetDirectionY * targetDirectionY);
        if (distance > range) return false;
        double targetAngle = Math.acos(targetDirectionX / distance) / Math.PI * 180;
        if (targetDirectionY > 0) targetAngle = 180 + (180 - targetAngle);

        double angle = Math.acos(directionX / Math.sqrt(directionX * directionX + directionY * directionY)) / Math.PI * 180;
        if (directionY > 0) angle = 180 + (180 - angle);

        return (targetAngle > angle - radius / 2 && targetAngle < angle + radius / 2) ||
                (targetAngle + 360 > angle - radius && targetAngle + 360 < angle + radius) ||
                (targetAngle > angle + 360 - radius && targetAngle < angle + 360 + radius);
    }
}
