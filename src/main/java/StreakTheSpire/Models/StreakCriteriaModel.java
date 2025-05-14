package StreakTheSpire.Models;

import StreakTheSpire.Utils.ConfigHelper;
import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

import java.lang.reflect.Type;

import static StreakTheSpire.StreakTheSpire.gson;

public class StreakCriteriaModel implements IConfigDataModel {
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
/*
    //region Config
    private final String RequireHeartKillConfigName = "require_heart_kill";
    private final String RequiredAscensionLevelConfigName = "required_ascension_level";
    private final String TrackedCharacterClassesConfigName = "tracked_character_classes";
    private final String AllowCustomSeedsConfigName = "allow_custom_seeds";
    private final String AllowDailiesConfigName = "allow_dailies";
    private final String AllowBetaConfigName = "allow_beta";
    private final String AllowDemoConfigName = "allow_demo";
    private final String AllowEndlessConfigName = "allow_endless";

    public void afterLoadFromConfig(SpireConfig config) {
        if(config.has(RequireHeartKillConfigName))
            requireHeartKill.setValue(config.getBool(RequireHeartKillConfigName));

        if(config.has(RequiredAscensionLevelConfigName))
            requiredAscensionLevel.setValue(config.getInt(RequiredAscensionLevelConfigName));

        if(config.has(TrackedCharacterClassesConfigName))
            allowCustomSeeds.setValue(config.getBool(AllowCustomSeedsConfigName));

        if(config.has(TrackedCharacterClassesConfigName))
            allowDailies.setValue(config.getBool(AllowDailiesConfigName));

        if(config.has(TrackedCharacterClassesConfigName))
            allowBeta.setValue(config.getBool(AllowBetaConfigName));

        if(config.has(TrackedCharacterClassesConfigName))
            allowDemo.setValue(config.getBool(AllowDemoConfigName));

        if(config.has(TrackedCharacterClassesConfigName))
            allowEndless.setValue(config.getBool(AllowEndlessConfigName));

        if(config.has(TrackedCharacterClassesConfigName)) {
            Type trackedCharacterClassesType = new TypeToken<PropertyList<String>>(){}.getType();
            trackedCharacterClasses = gson.fromJson(config.getString(TrackedCharacterClassesConfigName), trackedCharacterClassesType);
        }
    }

    public void beforeSaveToConfig(SpireConfig config) {
        config.setBool(RequireHeartKillConfigName, requireHeartKill.getValue());
        config.setInt(RequiredAscensionLevelConfigName, requiredAscensionLevel.getValue());
        config.setBool(AllowCustomSeedsConfigName, allowCustomSeeds.getValue());
        config.setBool(AllowDailiesConfigName, allowDailies.getValue());
        config.setBool(AllowBetaConfigName, allowBeta.getValue());
        config.setBool(AllowDemoConfigName, allowDemo.getValue());
        config.setString(TrackedCharacterClassesConfigName, gson.toJson(trackedCharacterClasses));
    }
    //endregion
    
 */
}
