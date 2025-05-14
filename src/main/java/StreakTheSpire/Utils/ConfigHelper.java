package StreakTheSpire.Utils;

import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class ConfigHelper {
    public static <T> T loadJson(SpireConfig config, String key, Type type) {
        if(!config.has(key))
            return null;

        return (T)gson.fromJson(config.getString(key), type);
    }


    public static <T> T loadJson(SpireConfig config, String key, Type type, T defaultValue) {
        if(!config.has(key))
            return defaultValue;

        T res = (T)gson.fromJson(config.getString(key), type);
        if(res == null)
            return defaultValue;

        return res;
    }

    public static <T> void saveJson(SpireConfig config, String key, T value) {
        if(value == null) {
            config.remove(key);
            return;
        }

        config.setString(key, gson.toJson(value));
    }
}
