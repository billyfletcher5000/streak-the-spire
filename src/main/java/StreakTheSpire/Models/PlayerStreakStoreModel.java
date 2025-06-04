package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyLinkedHashSet;

public class PlayerStreakStoreModel implements IModel, IConfigDataModel {
    public PropertyLinkedHashSet<PlayerStreakModel> playerToStreak = new PropertyLinkedHashSet<>();
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
