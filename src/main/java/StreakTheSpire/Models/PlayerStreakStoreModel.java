package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.PropertyHashSet;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreModel implements IConfigDataModel {
    public static final String RotatingPlayerIdentifier = "ROTATING";

    public PropertyHashSet<PlayerStreakModel> playerToStreak = new PropertyHashSet<>();
    public PlayerStreakModel rotatingPlayerStreakModel = null;

    //region IConfigModel
    private final String PlayerToStreakConfigName = "player_to_streak";
    private final String RotatingPlayerStreakConfigName = "rotating_player_streak";

    @Override
    public void loadFromConfig(SpireConfig config) {
        if(config.has(PlayerToStreakConfigName)) {
            Type playerToStreakType = new TypeToken<PropertyHashSet<PlayerStreakModel>>(){}.getType();
            playerToStreak = gson.fromJson(config.getString(PlayerToStreakConfigName), playerToStreakType);
        }

        if(config.has(RotatingPlayerStreakConfigName))
            rotatingPlayerStreakModel = gson.fromJson(config.getString(RotatingPlayerStreakConfigName), PlayerStreakModel.class);
    }

    @Override
    public void saveToConfig(SpireConfig config) {
        config.setString(PlayerToStreakConfigName, gson.toJson(playerToStreak));

        if(rotatingPlayerStreakModel != null)
            config.setString(RotatingPlayerStreakConfigName, gson.toJson(rotatingPlayerStreakModel));
    }
    //endregion
}
