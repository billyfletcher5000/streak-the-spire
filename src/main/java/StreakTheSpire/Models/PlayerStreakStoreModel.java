package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.PropertyHashSet;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreModel implements IConfigDataModel {
    public PropertyHashSet<PlayerStreakModel> playerToStreak = new PropertyHashSet<>();

    //region IConfigModel
    private final String PlayerToStreakConfigName = "PlayerToStreak";

    @Override
    public void loadFromConfig(SpireConfig config) {
        Type playerToStreakType = new TypeToken<PropertyHashSet<PlayerStreakModel>>(){}.getType();
        playerToStreak = gson.fromJson(config.getString(PlayerToStreakConfigName), playerToStreakType);
    }

    @Override
    public void saveToConfig(SpireConfig config) {
        config.setString(PlayerToStreakConfigName, gson.toJson(playerToStreak));
    }
    //endregion
}
