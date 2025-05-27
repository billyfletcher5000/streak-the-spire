package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;

public class DisplayPreferencesModel implements IConfigDataModel, IModel {
    public enum CharacterStyle {
        AnimatedIcon,
        StaticIcon,
        Text
    }

    public enum RenderLayer {
        PreRoom,
        Room,
        TopPanel,
        Default,
        AboveAll
    }

    public Property<CharacterStyle> characterStyle = new Property<>(CharacterStyle.AnimatedIcon);
    public Property<String> borderStyle = new Property<>(null);
    public Property<RenderLayer> renderLayer = new Property<>(RenderLayer.Default);
    public Property<String> fontIdentifier = new Property<>("Kreon_SDF_Outline_Shadow");

    public DisplayPreferencesModel() {}

    //region IConfigModel
    private String configID = "DisplayPreferencesModel";

    @Override
    public String getConfigID() {
        return configID;
    }

    @Override
    public void setConfigID(String ID) {
        configID = ID;
    }
    //endregion
}
