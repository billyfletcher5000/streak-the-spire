package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import static StreakTheSpire.StreakTheSpire.gson;

public class SavedPanelModel implements IModel, IConfigDataModel {
    public Property<Vector2> position = new Property<>(new Vector2(0, 0));
    public Property<Vector2> dimensions = new Property<>(new Vector2(200, 200));
    public Property<Vector2> scale = new Property<>(new Vector2(1, 1));
/*
    private static final String PositionConfigName = "position";
    private static final String DimensionsConfigName = "dimensions";
    private static final String ScaleConfigName = "scale";

    @Override
    public void afterLoadFromConfig(SpireConfig config) {
        position.setValue(gson.fromJson(config.getString(PositionConfigName), Vector2.class));
        dimensions.setValue(gson.fromJson(config.getString(DimensionsConfigName), Vector2.class));
        scale.setValue(gson.fromJson(config.getString(ScaleConfigName), Vector2.class));
    }

    @Override
    public void beforeSaveToConfig(SpireConfig config) {
        config.setString(PositionConfigName, gson.toJson(position.getValue()));
        config.setString(DimensionsConfigName, gson.toJson(dimensions.getValue()));
        config.setString(ScaleConfigName, gson.toJson(scale.getValue()));
    }

 */
}
