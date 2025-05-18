package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;

public class CharacterSkeletonDisplayModel extends CharacterDisplayModel {
    public Property<String> skeletonJsonUrl = new Property<>(null);
    public Property<String> skeletonAtlasUrl = new Property<>(null);
    public PropertyList<String> skeletonBonesToKeep = new PropertyList<>();
    public PropertyList<String> skeletonBonesToRemove = new PropertyList<>();
    public Property<Float> skeletonRotationAdjustment = new Property<>(0.0f);
}
