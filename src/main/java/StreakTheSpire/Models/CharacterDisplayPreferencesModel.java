package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;

public class CharacterDisplayPreferencesModel implements IConfigDataModel, IModel {
    public enum Style {
        AnimatedIcon,
        StaticIcon,
        Text
    }

    public Property<Style> style = new Property<>(Style.AnimatedIcon);

    public CharacterDisplayPreferencesModel() {}

    //region IConfigModel
    private String configID = "CharacterDisplayPreferencesModel";

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
