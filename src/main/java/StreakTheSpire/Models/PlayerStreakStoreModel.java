package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;

public class PlayerStreakStoreModel extends IModel implements IConfigDataModel {
    public static String RotatingPlayerIdentifier = "ROTATING";

    public PropertyHashSet<PlayerStreakModel> playerToStreak = new PropertyHashSet<>();
    public Property<PlayerStreakModel> rotatingPlayerStreakModel = new Property<>(null);
    public Property<SavedPanelModel> panelModel = new Property<>(new SavedPanelModel());

    public PlayerStreakStoreModel() {}
    public PlayerStreakStoreModel(String configID) { this.configID = configID; }

    //region IConfigModel
    private String configID = "PlayerStreakStoreModel";

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
