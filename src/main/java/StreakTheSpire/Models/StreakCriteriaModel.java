package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class StreakCriteriaModel implements IConfigDataModel {
    public static final int HeartKillFloorReached = 57;

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
            AbstractPlayer.PlayerClass.WATCHER.toString());

    //region Config
    private final String RequireHeartKillConfigName = "require_heart_kill";
    private final String RequiredAscensionLevelConfigName = "required_ascension_level";
    private final String TrackedCharacterClassesConfigName = "tracked_character_classes";
    private final String AllowCustomSeedsConfigName = "allow_custom_seeds";
    private final String AllowDailiesConfigName = "allow_dailies";
    private final String AllowBetaConfigName = "allow_beta";
    private final String AllowDemoConfigName = "allow_demo";
    private final String AllowEndlessConfigName = "allow_endless";

    public void loadFromConfig(SpireConfig config) {
        requireHeartKill.setValue(config.getBool(RequireHeartKillConfigName));
        requiredAscensionLevel.setValue(config.getInt(RequiredAscensionLevelConfigName));
        allowCustomSeeds.setValue(config.getBool(AllowCustomSeedsConfigName));
        allowDailies.setValue(config.getBool(AllowDailiesConfigName));
        allowBeta.setValue(config.getBool(AllowBetaConfigName));
        allowDemo.setValue(config.getBool(AllowDemoConfigName));
        allowEndless.setValue(config.getBool(AllowEndlessConfigName));
        Type trackedCharacterClassesType = new TypeToken<PropertyList<String>>(){}.getType();
        trackedCharacterClasses = gson.fromJson(config.getString(TrackedCharacterClassesConfigName), trackedCharacterClassesType);
    }

    public void saveToConfig(SpireConfig config) {
        config.setBool(RequireHeartKillConfigName, requireHeartKill.getValue());
        config.setInt(RequiredAscensionLevelConfigName, requiredAscensionLevel.getValue());
        config.setBool(AllowCustomSeedsConfigName, allowCustomSeeds.getValue());
        config.setBool(AllowDailiesConfigName, allowDailies.getValue());
        config.setBool(AllowBetaConfigName, allowBeta.getValue());
        config.setBool(AllowDemoConfigName, allowDemo.getValue());
        config.setString(TrackedCharacterClassesConfigName, gson.toJson(trackedCharacterClasses));
    }
    //endregion
}
