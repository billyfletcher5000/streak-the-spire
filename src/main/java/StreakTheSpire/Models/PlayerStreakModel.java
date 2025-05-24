package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;

public class PlayerStreakModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<Integer> highestStreak = new Property<>(0);
    public Property<String> highestStreakTimestamp = new Property<>(null);
    public Property<Integer> currentStreak = new Property<>(0);
    public Property<String> currentStreakTimestamp = new Property<>(null);
    public PropertyHashSet<String> processedFilenames = new PropertyHashSet<>();
    public Property<Integer> totalValidWins = new Property<>(0);
    public Property<Integer> totalValidLosses = new Property<>(0);

    public PlayerStreakModel() {}
}
