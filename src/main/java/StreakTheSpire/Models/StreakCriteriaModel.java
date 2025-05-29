package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class StreakCriteriaModel implements IModel, IConfigDataModel {
    public static final int HeartKillFloorReached = 56;

    public @ConfigProperty(type = Boolean.class) Property<Boolean> requireHeartKill = new Property<>(true);
    public @ConfigProperty(type = Integer.class) Property<Integer> requiredAscensionLevel = new Property<>(20);
    public @ConfigProperty(type = Boolean.class) Property<Boolean> allowCustomSeeds = new Property<>(false);
    public @ConfigProperty(type = Boolean.class) Property<Boolean> allowDailies = new Property<>(false);
    public @ConfigProperty(type = Boolean.class) Property<Boolean> allowBeta = new Property<>(false);
    public @ConfigProperty(type = Boolean.class) Property<Boolean> allowDemo = new Property<>(false);
    public @ConfigProperty(type = Boolean.class) Property<Boolean> allowEndless = new Property<>(false);
    public @ConfigProperty(type = String.class) PropertyList<String> trackedCharacterClasses = new PropertyList<>(
            AbstractPlayer.PlayerClass.IRONCLAD.toString(),
            AbstractPlayer.PlayerClass.THE_SILENT.toString(),
            AbstractPlayer.PlayerClass.DEFECT.toString(),
            AbstractPlayer.PlayerClass.WATCHER.toString()
    );
    public @ConfigProperty(type = Boolean.class) Property<Boolean> trackRotating = new Property<>(true);

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
