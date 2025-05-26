package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;

public class DisplayPreferencesModel implements IConfigDataModel, IModel {
    public enum CharacterStyle {
        AnimatedIcon,
        StaticIcon,
        Text
    }

    public Property<CharacterStyle> characterStyle = new Property<>(CharacterStyle.AnimatedIcon);
    public Property<String> borderStyle = new Property<>(null);

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
