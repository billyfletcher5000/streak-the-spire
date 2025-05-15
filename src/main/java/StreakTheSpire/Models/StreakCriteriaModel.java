package StreakTheSpire.Models;

import StreakTheSpire.Utils.ConfigHelper;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class StreakCriteriaModel extends IModel implements IConfigDataModel {
    public static final int HeartKillFloorReached = 56;

    public Property<Boolean> requireHeartKill = new Property<>(true);
    public Property<Integer> requiredAscensionLevel = new Property<>(20);
    public Property<Boolean> allowCustomSeeds = new Property<>(false);
    public Property<Boolean> allowDailies = new Property<>(false);
    public Property<Boolean> allowBeta = new Property<>(false);
    public Property<Boolean> allowDemo = new Property<>(false);
    public Property<Boolean> allowEndless = new Property<>(false);
    public PropertyList<String> trackedCharacterClasses = new PropertyList<>(
            AbstractPlayer.PlayerClass.IRONCLAD.toString(),
            AbstractPlayer.PlayerClass.THE_SILENT.toString(),
            AbstractPlayer.PlayerClass.DEFECT.toString(),
            AbstractPlayer.PlayerClass.WATCHER.toString()
    );

    public StreakCriteriaModel() {}
    public StreakCriteriaModel(String configID) { this.configID = configID; }

    //region IConfigModel
    private String configID = "StreakCriteriaModel";

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
