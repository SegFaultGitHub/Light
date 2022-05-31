package Locale;

import Utils.Utils;
import org.json.simple.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public abstract class Locale {
    private static NumberFormat numberFormat;
    private static HashMap<String, HashMap<String, String>> locals;
    private static String language;

    public static void initialize() throws Exception {
        locals = new HashMap<>();
        String[] languages = new String[]{"fr", "en"};
        JSONObject rootJson = Utils.readJSON("data/localization/localization.json");
        rootJson.entrySet().forEach(entry -> {
            Map.Entry<String, JSONObject> node = (Map.Entry) entry;
            locals.put(node.getKey(), new HashMap<>()); // Put key
            for (String language : languages) {
                JSONObject r = node.getValue();
                locals.get(node.getKey()).put(language, (String) r.get(language));
            }
        });

        changeLanguage("en");
    }

    public static void changeLanguage(String newLanguage) {
        language = newLanguage;
        numberFormat = NumberFormat.getInstance(new java.util.Locale(language));
    }

    public static String getLocal(String key) {
        if (locals.get(key) == null) return "Missing entry for '" + key + "'";
        String local = locals.get(key).get(language);
        if (local == null) return "Missing " + language + "translation for '" + key + "'";
        return local;
    }

    public static String formatNumber(int n) {
        return numberFormat.format(n);
    }
}
