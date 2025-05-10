package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyHashSet;

public class PlayerStreakModel {
    public Property<String> playerClass = new Property<>(null);
    public Property<Integer> highestStreak = new Property<>(0);
    public Property<String> highestStreakTimestamp = new Property<>(null);
    public Property<Integer> currentStreak = new Property<>(0);
    public Property<String> currentStreakTimestamp = new Property<>(null);
    public PropertyHashSet<String> processedFilenames = new PropertyHashSet<>();
}
