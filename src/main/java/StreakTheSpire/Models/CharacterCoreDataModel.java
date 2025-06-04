package StreakTheSpire.Models;

import StreakTheSpire.Utils.Properties.Property;
import com.badlogic.gdx.graphics.Color;

public class CharacterCoreDataModel implements IModel {
    public Property<String> identifier = new Property<>(null);
    public Property<String> localisationID = new Property<>(null);
    public Property<Color> streakTextColor = new Property<>(new Color(0.95f, 0.95f, 0.95f, 1.0f));
    public Property<Integer> displayOrderPriority = new Property<>(Integer.MAX_VALUE);

    public CharacterCoreDataModel() {}
}
