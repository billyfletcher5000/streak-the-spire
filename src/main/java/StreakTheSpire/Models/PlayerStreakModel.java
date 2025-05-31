package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;
import StreakTheSpire.Utils.Properties.PropertyList;

public class PlayerStreakModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<Integer> highestStreak = new Property<>(0);
    public Property<String> highestStreakTimestamp = new Property<>(null);
    public Property<Integer> currentStreak = new Property<>(0);
    public Property<String> currentStreakTimestamp = new Property<>(null);
    public PropertyList<String> currentStreakCharacterIDs = new PropertyList<>();
    public PropertyHashSet<String> processedFilenames = new PropertyHashSet<>();
    public Property<Integer> totalValidWins = new Property<>(0);
    public Property<Integer> totalValidLosses = new Property<>(0);

    public PlayerStreakModel() {}

    public void reset() {
        highestStreak.set(0);
        highestStreakTimestamp.set(null);
        currentStreak.set(0);
        currentStreakTimestamp.set(null);
        currentStreakCharacterIDs.clear();
        processedFilenames.clear();
        totalValidWins.set(0);
        totalValidLosses.set(0);
    }

    public PlayerStreakModel cpy() {
        PlayerStreakModel cpy = new PlayerStreakModel();
        cpy.identifier.set(identifier.get());
        cpy.highestStreak.set(this.highestStreak.get());
        cpy.highestStreakTimestamp.set(this.highestStreakTimestamp.get());
        cpy.currentStreak.set(this.currentStreak.get());
        cpy.currentStreakTimestamp.set(this.currentStreakTimestamp.get());
        cpy.currentStreakCharacterIDs.addAll(this.currentStreakCharacterIDs);
        cpy.processedFilenames.addAll(this.processedFilenames);
        cpy.totalValidWins.set(this.totalValidWins.get());
        cpy.totalValidLosses.set(this.totalValidLosses.get());
        return cpy;
    }

    public void set(PlayerStreakModel other) {
        this.identifier.set(other.identifier.get());
        this.highestStreak.set(other.highestStreak.get());
        this.highestStreakTimestamp.set(other.highestStreakTimestamp.get());
        this.currentStreak.set(other.currentStreak.get());
        this.currentStreakTimestamp.set(other.currentStreakTimestamp.get());
        this.currentStreakCharacterIDs.clear();
        this.currentStreakCharacterIDs.addAll(other.currentStreakCharacterIDs);
        this.processedFilenames.clear();
        this.processedFilenames.addAll(other.processedFilenames);
        this.totalValidWins.set(other.totalValidWins.get());
        this.totalValidLosses.set(other.totalValidLosses.get());
    }
}
