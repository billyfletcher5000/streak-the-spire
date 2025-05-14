package StreakTheSpire.Models;

import StreakTheSpire.Utils.ConfigHelper;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class PlayerStreakStoreModel implements IModel, IConfigDataModel {
    public static String RotatingPlayerIdentifier = "ROTATING";

    public PropertyHashSet<PlayerStreakModel> playerToStreak = new PropertyHashSet<>();
    public Property<PlayerStreakModel> rotatingPlayerStreakModel = new Property<>(null);
    public Property<SavedPanelModel> panelModel = new Property<>(new SavedPanelModel());

    public PlayerStreakStoreModel() {}
/*

    //region IConfigModel
    private static final String PlayerToStreakConfigName = "player_to_streak";
    private static final String RotatingPlayerStreakConfigName = "rotating_player_streak";
    private static final String PanelModelConfigName = "panel_model";

    @Override
    public void afterLoadFromConfig(SpireConfig config) {
        if(config.has(PlayerToStreakConfigName)) {
            Type playerToStreakType = new TypeToken<PropertyHashSet<PlayerStreakModel>>(){}.getType();
            playerToStreak = gson.fromJson(config.getString(PlayerToStreakConfigName), playerToStreakType);
        }

        rotatingPlayerStreakModel.setValue(ConfigHelper.loadJson(config, RotatingPlayerStreakConfigName, PlayerStreakModel.class));
        panelModel.setValue(ConfigHelper.loadJson(config, PanelModelConfigName, SavedPanelModel.class, panelModel.getValue()));
    }

    @Override
    public void beforeSaveToConfig(SpireConfig config) {
        config.setString(PlayerToStreakConfigName, gson.toJson(playerToStreak));
        ConfigHelper.saveJson(config, RotatingPlayerStreakConfigName, rotatingPlayerStreakModel.getValue());
        ConfigHelper.saveJson(config, PanelModelConfigName, panelModel.getValue());
    }
    //endregion

 */
}
