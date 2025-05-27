package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import StreakTheSpire.Utils.Properties.PropertyList;
import com.badlogic.gdx.math.Vector2;

public class CharacterSkeletonDisplayModel extends CharacterDisplayModel {
    public Property<Vector2> baseDimensions = new Property<>(new Vector2(64, 64));
    public Property<Vector2> skeletonOffset = new Property<>(new Vector2(0, 0)); // Note: This is applied BEFORE rotation
    public Property<String> skeletonJsonUrl = new Property<>(null);
    public Property<String> skeletonAtlasUrl = new Property<>(null);
    public PropertyList<String> skeletonBonesToKeep = new PropertyList<>();
    public PropertyList<String> skeletonBonesToRemove = new PropertyList<>();
    public Property<String> skeletonIdleAnimationName = new Property<>("Idle");
    public Property<String> skeletonHitAnimationName = new Property<>("Hit");
    public Property<Float> skeletonIdleAnimationSpeed = new Property<>(1.0f);
    public Property<Float> skeletonHitAnimationSpeed = new Property<>(1.0f);
    public Property<Float> skeletonAnimationMixDuration = new Property<>(0.0f);
    public Property<Float> skeletonRotationAdjustment = new Property<>(0.0f);
}
